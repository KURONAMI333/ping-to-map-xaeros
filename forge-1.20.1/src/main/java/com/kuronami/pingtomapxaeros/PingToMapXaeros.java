package com.kuronami.pingtomapxaeros;

import com.mojang.logging.LogUtils;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import org.slf4j.Logger;

/**
 * Ping to Map: Xaero's edition (Forge 1.20.1, CLIENT only).
 *
 * <p>Ping-Wheel で受信した ping を Xaero's Minimap / World Map の
 * 一時 waypoint として「副次的・プロンプトなし・30 秒で自動消滅」で立てる。
 *
 * <p>Forge 1.20.1 の {@code @Mod} は value のみ。CLIENT 限定は mods.toml の
 * {@code side="CLIENT"} で表現する。
 */
@Mod(PingToMapXaeros.MODID)
public class PingToMapXaeros {

    public static final String MODID = "pingtomapxaeros";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PingToMapXaeros() {
        // CLIENT 専用 MOD
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
        // Mixin は src/main/resources/pingtomapxaeros.mixins.json から自動ロード
        LOGGER.info("Ping to Map: Xaero's edition (Forge 1.20.1) initialized");
    }
}
