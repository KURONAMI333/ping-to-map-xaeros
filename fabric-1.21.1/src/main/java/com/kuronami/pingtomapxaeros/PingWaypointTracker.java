package com.kuronami.pingtomapxaeros;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * 各プレイヤーの ping 由来 waypoint の寿命を管理する (Fabric 1.21.1)。
 *
 * <p>Fabric API では event subscription を {@link PingToMapXaerosFabric#onInitializeClient}
 * から callback 登録するスタイル。本クラスは {@code static} ハンドラだけ提供。
 */
public final class PingWaypointTracker {

    private PingWaypointTracker() {}

    private static final Map<UUID, Tracked> ACTIVE = new HashMap<>();

    /** expireAtNanos: {@link System#nanoTime()} ベースの単調時計絶対時刻。 */
    private record Tracked(Object waypoint, Object waypointSet, long expireAtNanos) {}

    public static void schedule(UUID author, Object waypoint, Object waypointSet, int lifetimeSec) {
        if (waypoint == null || waypointSet == null) return;
        if (lifetimeSec < 0) return;

        Tracked prev = ACTIVE.remove(author);
        if (prev != null) {
            XaeroReflect.removeWaypoint(prev.waypointSet, prev.waypoint);
        }

        // System.nanoTime() は monotonic — OS の NTP 補正で時計ジャンプしても影響を受けない。
        long expireAt = System.nanoTime() + lifetimeSec * 1_000_000_000L;
        ACTIVE.put(author, new Tracked(waypoint, waypointSet, expireAt));
    }

    /** Fabric {@code ClientTickEvents.END_CLIENT_TICK} から毎 tick 呼ばれる。 */
    public static void onClientTick() {
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

    /** Fabric {@code ClientPlayConnectionEvents.DISCONNECT} から呼ばれる。 */
    public static void clearAll() {
        for (Tracked t : ACTIVE.values()) {
            XaeroReflect.removeWaypoint(t.waypointSet, t.waypoint);
        }
        ACTIVE.clear();
    }
}
