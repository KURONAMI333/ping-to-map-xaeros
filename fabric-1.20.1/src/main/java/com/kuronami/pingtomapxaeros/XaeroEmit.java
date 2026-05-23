package com.kuronami.pingtomapxaeros;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

/**
 * Ping を受信したクライアントで、Xaero's Minimap に <b>プロンプトなし・一時 waypoint</b> を立てる。
 *
 * <p>JM 版 P2M と同じ UX 仕様:
 * <ul>
 *   <li>「ping した瞬間に副次的に waypoint が立つ」 — UI 介入なし</li>
 *   <li>「30 秒で自動消滅」 — {@link Config#WAYPOINT_LIFETIME_SEC}</li>
 *   <li>「マルチプレイヤーでチーム共有」 — Ping-Wheel が S2C で全クライアントに配信する構造のため、
 *       本実装は各クライアントで個別に一時 waypoint を立てるだけで自然に共有される</li>
 * </ul>
 *
 * <p>実装は {@link XaeroReflect} 経由で Xaero internal API
 * ({@code Waypoint} 構築 → {@code WaypointSet.add}) を直叩き。プロンプト/編集画面を出さない。
 * 寿命管理は {@link PingWaypointTracker} に委譲。
 *
 * <p>Xaero's 不在 / Xaero's セッション未起動の場合は {@link XaeroReflect} が null を返し、
 * 本クラスは静かに何もしないで終了（crash しない）。
 */
public final class XaeroEmit {

    /** ブランド色（Xaero {@code WaypointColor.PURPLE} = index 13）。 */
    private static final String BRAND_COLOR_ENUM = "PURPLE";
    /** ミニマップ上の略号（1-3 chars）。 */
    private static final String INITIALS = "P";

    private XaeroEmit() {}

    /**
     * Ping 受信時に呼ばれる。一時 waypoint を立て、寿命管理に登録する。
     *
     * @param authorUuid ping を打ったプレイヤー UUID
     * @param pos        ping 位置
     * @param dim        ping が発生したディメンション（現状は使ってないが将来の dim 固有処理用に保持）
     */
    public static void emit(UUID authorUuid, Vec3 pos, ResourceKey<Level> dim) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null) return;

        // 表示名構築: 自分なら "You's Ping"、他人なら "<Name>'s Ping"
        String wpName = resolveAuthorName(mc, authorUuid) + "'s Ping";

        // BlockPos.containing(Vec3) と同じ規則: Math.floor で「立ってるブロック」座標を取る。
        // Math.round はオーバーフロー時 (Integer.MAX 近辺の遠隔 dim) に int キャストで壊れる。
        int x = (int) Math.floor(pos.x);
        int y = (int) Math.floor(pos.y);
        int z = (int) Math.floor(pos.z);

        // Xaero internal state を取得
        Object minimapWorld = XaeroReflect.getCurrentMinimapWorld();
        if (minimapWorld == null) return; // Xaero 不在 or セッション未起動
        Object waypointSet = XaeroReflect.getCurrentWaypointSet(minimapWorld);
        if (waypointSet == null) return;

        // Waypoint 構築 (temporary=true, yIncluded=true)
        Object waypoint = XaeroReflect.newWaypoint(
                x, y, z, wpName, INITIALS, BRAND_COLOR_ENUM, "NORMAL", true, true
        );
        if (waypoint == null) return;

        // WaypointSet に先頭挿入 (新しい ping waypoint が waypoint list の上に見えるように)
        if (!XaeroReflect.addWaypoint(waypointSet, waypoint, true)) return;

        // 寿命管理に登録: lifetimeSec 秒後に自動削除
        int lifetimeSec = Config.WAYPOINT_LIFETIME_SEC.get();
        PingWaypointTracker.schedule(authorUuid, waypoint, waypointSet, lifetimeSec);
    }

    /**
     * Ping を打ったプレイヤーの表示名を解決する。
     *
     * <p>自分も他人も統一して **ユーザー名（GameProfile name）** を使う。
     * 視点に依存せず「kuro's Ping」のような一意の名前で全員の画面に見える方が
     * チーム coop で混乱しない（JM 版 P2M の挙動と整合）。
     *
     * <p>UUID 解決に失敗した時のみ {@code "Player"} にフォールバック。
     */
    private static String resolveAuthorName(Minecraft mc, UUID id) {
        if (mc.level == null) return "Player";
        // 自分の場合
        if (mc.player != null && mc.player.getUUID().equals(id)) {
            return mc.player.getGameProfile().getName();
        }
        // 他人の場合
        if (mc.level.getPlayerByUUID(id) instanceof AbstractClientPlayer p) {
            return p.getGameProfile().getName();
        }
        return "Player";
    }
}
