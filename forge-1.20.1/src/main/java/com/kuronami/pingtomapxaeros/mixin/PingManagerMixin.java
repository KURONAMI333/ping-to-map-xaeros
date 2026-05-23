package com.kuronami.pingtomapxaeros.mixin;

import com.kuronami.pingtomapxaeros.Config;
import com.kuronami.pingtomapxaeros.XaeroEmit;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

import nx.pingwheel.common.core.PingManager;
import nx.pingwheel.common.network.PingLocationS2CPacket;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/**
 * Ping-Wheel の {@code PingManager.acceptPingPacket} フック。
 *
 * <p>Ping-Wheel は公式 API を提供していないため、本 MOD では Mixin で
 * メソッドの先頭に {@code @Inject} して、受信した ping packet を傍受する。
 * 取得した {@code pos} / {@code author} を {@link XaeroEmit} へ渡し、
 * Xaero internal API (Reflection 直叩き) で一時 waypoint を立てる。
 *
 * <p><b>Threading</b>: Ping-Wheel 本来の {@code acceptPingPacket} は <b>netty I/O thread</b>
 * で実行される (Ping-Wheel 自身も末尾で {@code Minecraft.execute} 経由で main thread に
 * 再ディスパッチしている)。本 Mixin は {@code @At(HEAD)} で割り込むため netty thread で
 * 動く。{@link Minecraft}/{@code mc.level}/{@code mc.player}/Xaero state はすべて main
 * thread でのみアクセス可能なため、{@code Minecraft.getInstance().execute(...)} で
 * main thread にマーシャルしてから処理する。
 *
 * <p>{@code @Inject} で「割り込む」だけで Ping-Wheel 本来の処理は止めない
 * （{@code ci.cancel} しない）。すべての例外を握りつぶし、Ping-Wheel の通常動作を阻害しない。
 */
@Mixin(PingManager.class)
public abstract class PingManagerMixin {

    @Inject(
            method = "acceptPingPacket",
            at = @At("HEAD")
    )
    private static void pingtomapxaeros$onPingReceived(PingLocationS2CPacket packet, CallbackInfo ci) {
        try {
            // packet は record (immutable)。netty thread で値だけ抜き取り、
            // main thread に処理を全部委譲する。
            final UUID author = packet.author();
            final Vec3 pos = packet.pos();

            Minecraft.getInstance().execute(() -> {
                try {
                    if (!Config.ENABLED.get()) return;

                    Minecraft mc = Minecraft.getInstance();
                    if (mc == null || mc.level == null) return;

                    // registerOwnPings=false の時は自分の ping を無視する。
                    if (!Config.REGISTER_OWN_PINGS.get()
                            && mc.player != null
                            && mc.player.getUUID().equals(author)) {
                        return;
                    }

                    XaeroEmit.emit(author, pos, mc.level.dimension());
                } catch (Throwable ignored) {
                    // Xaero 連携で何が起きても Ping-Wheel 通常動作を阻害しない。
                }
            });
        } catch (Throwable t) {
            // Mixin 自体が落ちないように二重ガード。
        }
    }
}
