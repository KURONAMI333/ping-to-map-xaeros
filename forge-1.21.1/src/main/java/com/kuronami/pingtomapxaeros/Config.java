package com.kuronami.pingtomapxaeros;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Ping to Map: Xaero's edition の CLIENT 設定 (Forge 1.21.1)。
 */
public final class Config {
    private static final ForgeConfigSpec.Builder B = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue ENABLED = B
            .comment("Master switch. If false, no waypoint prompts are emitted on ping.")
            .define("feature.enabled", true);

    public static final ForgeConfigSpec.BooleanValue REGISTER_OWN_PINGS = B
            .comment("If true, your own pings also create a temporary waypoint. If false, only teammates' pings do.")
            .define("feature.registerOwnPings", true);

    public static final ForgeConfigSpec.IntValue WAYPOINT_LIFETIME_SEC = B
            .comment(
                    "How long the auto-created waypoint stays on the map, in seconds. Default 30s."
            )
            .defineInRange("appearance.waypointLifetimeSec", 30, 1, 600);

    static final ForgeConfigSpec SPEC = B.build();

    private Config() {}
}
