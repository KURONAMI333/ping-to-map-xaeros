package com.kuronami.pingtomapxaeros;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * 各プレイヤーの ping 由来 waypoint の寿命を管理する (Forge 1.20.1)。
 *
 * <p>JM 版 P2M と同じ「ping した瞬間に副次的に一時 waypoint が立つ・30 秒で消える」を再現:
 * <ul>
 *   <li>{@link #schedule}: emit 時に {@link UUID author} 単位で 1 つだけ追跡。
 *       同じ author の連続 ping は古い waypoint を即削除して新規だけ残す</li>
 *   <li>{@link #onClientTick}: 毎 tick (render thread) で expire 時刻を過ぎた waypoint を削除</li>
 *   <li>logout / level unload: 全エントリを即削除（リーク防止）</li>
 * </ul>
 *
 * <p>本クラスは render thread から呼ばれる前提
 * （PingManagerMixin の呼び出しコンテキストが client packet handler = render thread）。
 */
@Mod.EventBusSubscriber(modid = PingToMapXaeros.MODID, value = Dist.CLIENT)
public final class PingWaypointTracker {

    private PingWaypointTracker() {}

    /** author UUID → 追跡対象エントリ。 */
    private static final Map<UUID, Tracked> ACTIVE = new HashMap<>();

    /** expireAtNanos: {@link System#nanoTime()} ベースの単調時計絶対時刻。 */
    private record Tracked(Object waypoint, Object waypointSet, long expireAtNanos) {}

    public static void schedule(UUID author, Object waypoint, Object waypointSet, int lifetimeSec) {
        if (waypoint == null || waypointSet == null) return;
        if (lifetimeSec < 0) return; // 永続: 自動削除しない

        Tracked prev = ACTIVE.remove(author);
        if (prev != null) {
            XaeroReflect.removeWaypoint(prev.waypointSet, prev.waypoint);
        }

        // System.nanoTime() は monotonic — OS の NTP 補正で時計ジャンプしても影響を受けない。
        long expireAt = System.nanoTime() + lifetimeSec * 1_000_000_000L;
        ACTIVE.put(author, new Tracked(waypoint, waypointSet, expireAt));
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent evt) {
        if (evt.phase != TickEvent.Phase.END) return;
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

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut evt) {
        clearAll();
    }

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
