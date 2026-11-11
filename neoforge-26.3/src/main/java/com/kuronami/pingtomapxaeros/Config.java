package com.kuronami.pingtomapxaeros;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Ping to Map: Xaero's edition の CLIENT 設定。
 *
 * <p>本 MOD は Ping-Wheel のクライアントサイド処理を Mixin でフックするため
 * 設定はすべて CLIENT 側に置く。
 *
 * <p>Xaero's Minimap の現在の waypoint set へ自動登録し、寿命は本 MOD が管理する。
 * World Map は併用できるが、Minimap が無いと登録先を取得できない。
 */
public final class Config {
    private static final ModConfigSpec.Builder B = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLED = B
            .comment("Master switch. If false, no waypoint is auto-created on ping.")
            .define("feature.enabled", true);

    public static final ModConfigSpec.BooleanValue REGISTER_OWN_PINGS = B
            .comment("If true, your own pings also create a temporary waypoint. If false, only teammates' pings do.")
            .define("feature.registerOwnPings", true);

    public static final ModConfigSpec.BooleanValue SYNC_WITH_PING_WHEEL = B
            .comment(
                    "If true (default), the auto-created waypoint disappears at the same time as the Ping-Wheel ping:",
                    "its lifetime follows Ping-Wheel's own pingDuration setting, so the in-world ping and the",
                    "map waypoint vanish together. If false, the fixed 'waypointLifetimeSec' below is used."
            )
            .define("appearance.syncWithPingWheel", true);

    public static final ModConfigSpec.IntValue WAYPOINT_LIFETIME_SEC = B
            .comment(
                    "Manual waypoint lifetime in seconds. Only used when 'syncWithPingWheel' is false.",
                    "Set to -1 for permanent waypoints (not recommended for ping use case)."
            )
            .defineInRange("appearance.waypointLifetimeSec", 30, -1, 600);

    static final ModConfigSpec SPEC = B.build();

    private Config() {}
}
