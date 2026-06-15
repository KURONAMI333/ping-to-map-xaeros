package com.kuronami.pingtomapxaeros;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * 各プレイヤーの ping 由来 waypoint の寿命を管理する。
 *
 * <p>JM 版 P2M と同じ「ping した瞬間に副次的に一時 waypoint が立つ・30 秒で消える」を再現するため:
 * <ul>
 *   <li>{@link #schedule}: emit 時に {@link UUID author} 単位で 1 つだけ追跡。
 *       同じ author の連続 ping は古い waypoint を即削除して新規だけ残す</li>
 *   <li>{@link #onClientTick}: 毎 tick (render thread) で expire 時刻を過ぎた waypoint を削除</li>
 *   <li>logout / level unload: 全エントリを即削除（リーク防止）</li>
 * </ul>
 *
 * <p>本クラスは render thread から呼ばれることを前提とする
 * （PingManagerMixin の呼び出しコンテキストが client packet handler = render thread）。
 * Map は HashMap で十分（並行アクセスなし）。
 */
@EventBusSubscriber(modid = PingToMapXaeros.MODID, value = net.neoforged.api.distmarker.Dist.CLIENT)
public final class PingWaypointTracker {

    private PingWaypointTracker() {}

    /** author UUID → 追跡対象エントリ。 */
    private static final Map<UUID, Tracked> ACTIVE = new HashMap<>();

    /** expireAtNanos: {@link System#nanoTime()} ベースの単調時計絶対時刻。 */
    private record Tracked(Object waypoint, Object waypointSet, long expireAtNanos) {}

    /**
     * 新たに追加した waypoint を寿命管理に登録する。
     *
     * <p>同じ {@code author} が既に追跡中ならその waypoint を即削除してから登録。
     * 連続 ping で waypoint が増殖しない。
     *
     * @param author      ping を打ったプレイヤーの UUID
     * @param waypoint    Xaero {@code Waypoint} オブジェクト
     * @param waypointSet 追加先の {@code WaypointSet}
     * @param lifetimeSec 寿命（秒）。-1 で永続（追跡せず即 return）
     */
    public static void schedule(UUID author, Object waypoint, Object waypointSet, int lifetimeSec) {
        if (waypoint == null || waypointSet == null) return;
        if (lifetimeSec < 0) return; // 永続: 自動削除しない

        // 同じ author の前回 ping waypoint を即削除（重複防止）
        Tracked prev = ACTIVE.remove(author);
        if (prev != null) {
            XaeroReflect.removeWaypoint(prev.waypointSet, prev.waypoint);
        }

        // System.nanoTime() は monotonic — OS の NTP 補正で時計ジャンプしても影響を受けない。
        long expireAt = System.nanoTime() + lifetimeSec * 1_000_000_000L;
        ACTIVE.put(author, new Tracked(waypoint, waypointSet, expireAt));
    }

    /**
     * 毎 tick 呼ばれ、expire 時刻を過ぎたエントリを削除する。
     *
     * <p>Render thread で動作。Xaero の WaypointSet 操作はすべて render thread でやる前提。
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post evt) {
        if (ACTIVE.isEmpty()) return;
        long now = System.nanoTime();
        Iterator<Map.Entry<UUID, Tracked>> it = ACTIVE.entrySet().iterator();
        while (it.hasNext()) {
            Tracked t = it.next().getValue();
            // 単調時計の比較は long 差分の符号で判定 (オーバーフロー耐性)
            if (now - t.expireAtNanos >= 0L) {
                XaeroReflect.removeWaypoint(t.waypointSet, t.waypoint);
                it.remove();
            }
        }
    }

    /**
     * プレイヤーがサーバから切断したとき、追跡中の waypoint を全削除する。
     * Minecraft のクライアント実装ではログアウト = ワールド離脱と同義。
     */
    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut evt) {
        clearAll();
    }

    /**
     * Level unload 時のセーフティネット（シングルプレイヤーで別ワールド遷移など）。
     */
    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload evt) {
        if (Minecraft.getInstance() == null) return;
        if (evt.getLevel() == Minecraft.getInstance().level) {
            clearAll();
        }
    }

    private static void clearAll() {
        for (Tracked t : ACTIVE.values()) {
            XaeroReflect.removeWaypoint(t.waypointSet, t.waypoint);
        }
        ACTIVE.clear();
    }
}
