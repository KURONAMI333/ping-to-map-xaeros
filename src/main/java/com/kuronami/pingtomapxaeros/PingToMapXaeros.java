package com.kuronami.pingtomapxaeros;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

import org.slf4j.Logger;

/**
 * Ping to Map: Xaero's Minimap & Ping Wheel Addon.
 *
 * Ping-Wheel で誰かが ping した位置を、Xaero's Minimap / World Map の
 * waypoint として登録するよう促す chat-share 行をクライアントに流す。
 * チーム coop で「あそこ来て！」が地図上で一目でわかるようになる。
 *
 * 仕組み:
 *  - Mixin で Ping-Wheel の {@code PingManager.acceptPingPacket(PingLocationS2CPacket)} をフック
 *  - 受信した ping の座標 + author UUID を取得
 *  - Xaero's の auto-detect 書式 {@code xaero-waypoint:...} の文字列を組み立てる
 *  - {@code ChatListener.handleSystemMessage} 経由でクライアントの chat pipeline に流す
 *    → Xaero's の {@code MixinChatListener} が拾って「Add to waypoints?」プロンプトを表示
 *
 * 注: Ping-Wheel は公式 API を持たないため Mixin での実装。
 * Xaero's 不在でも chat 行は単なる文字列として表示されるだけで crash しない。
 */
@Mod(value = PingToMapXaeros.MODID, dist = net.neoforged.api.distmarker.Dist.CLIENT)
public class PingToMapXaeros {

    public static final String MODID = "pingtomapxaeros";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PingToMapXaeros(IEventBus modEventBus, ModContainer modContainer) {
        // CLIENT 専用 MOD: サーバ側では何もしない
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);

        // Config GUI 自動生成
        modContainer.registerExtensionPoint(
                net.neoforged.neoforge.client.gui.IConfigScreenFactory.class,
                net.neoforged.neoforge.client.gui.ConfigurationScreen::new
        );
    }
}
