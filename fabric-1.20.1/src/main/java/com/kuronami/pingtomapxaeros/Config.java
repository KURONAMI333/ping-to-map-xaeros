package com.kuronami.pingtomapxaeros;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Ping to Map: Xaero's edition の CLIENT 設定 (Fabric 1.20.1, Forge Config API Port 経由)。
 */
public final class Config {
    private static final ForgeConfigSpec.Builder B = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue ENABLED = B
            .comment("Master switch. If false, no waypoint prompts are emitted on ping.")
            .define("feature.enabled", true);

    public static final ForgeConfigSpec.BooleanValue REGISTER_OWN_PINGS = B
            .comment("If true, your own pings also create a temporary waypoint. If false, only teammates' pings do.")
            .define("feature.registerOwnPings", true);

    public static final ForgeConfigSpec.BooleanValue SYNC_WITH_PING_WHEEL = B
            .comment(
                    "If true (default), the auto-created waypoint disappears at the same time as the Ping-Wheel ping:",
                    "its lifetime follows Ping-Wheel's own pingDuration setting, so the in-world ping and the",
                    "map waypoint vanish together. If false, the fixed 'waypointLifetimeSec' below is used."
            )
            .define("appearance.syncWithPingWheel", true);

    public static final ForgeConfigSpec.IntValue WAYPOINT_LIFETIME_SEC = B
            .comment(
                    "Manual waypoint lifetime in seconds. Only used when 'syncWithPingWheel' is false.",
                    "Set to -1 for permanent waypoints (not recommended for ping use case)."
            )
            .defineInRange("appearance.waypointLifetimeSec", 30, -1, 600);

    static final ForgeConfigSpec SPEC = B.build();

    private Config() {}
}
