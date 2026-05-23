package com.kuronami.pingtomapxaeros;

import com.mojang.logging.LogUtils;

import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraftforge.fml.config.ModConfig;

import org.slf4j.Logger;

/**
 * Ping to Map: Xaero's edition - Fabric 1.21.1 entry (CLIENT only)。
 *
 * <p>Mixin 経由で Ping-Wheel の {@code PingManager.acceptPingPacket} をフック。
 * Tick / disconnect イベントは Fabric API 経由で {@link PingWaypointTracker} に委譲。
 */
public class PingToMapXaerosFabric implements ClientModInitializer {

    public static final String MODID = "pingtomapxaeros";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitializeClient() {
        // Config 登録 (FCAP 経由)
        ForgeConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.CLIENT, Config.SPEC);

        // 寿命管理: tick で expire チェック、disconnect で全削除
        ClientTickEvents.END_CLIENT_TICK.register(mc -> PingWaypointTracker.onClientTick());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, mc) -> PingWaypointTracker.clearAll());

        LOGGER.info("Ping to Map: Xaero's edition (Fabric 1.21.1) initialized");
    }
}
