package com.cheat.bypass.grim;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.PacketEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.util.MathUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

/**
 * Grim velocity.
 *
 * <p>Rewrites incoming {@link EntityVelocityUpdateS2CPacket}s that target the local player.
 * The packet is cancelled and the velocity is applied directly with a random factor between
 * 0.6 and 0.8, so part of the knockback is kept instead of zeroed. This defeats Grim's
 * velocity / knockback consistency check while still letting the player move.
 */
public final class VelocityGrim extends BypassModule {

    private double minFactor = 0.60d;
    private double maxFactor = 0.80d;

    public VelocityGrim() {
        super("VelocityGrim", "Grim velocity: keeps 60-80% of incoming knockback.",
                ModuleCategory.MOVEMENT, Anticheat.GRIM);
    }

    @Override
    public void onPacket(PacketEvent event) {
        if (!event.isIncoming() || !(event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet)) {
            return;
        }
        MinecraftClient client = client();
        if (client.player == null || packet.getEntityId() != client.player.getId()) {
            return;
        }
        event.cancel();
        double factor = MathUtil.random(minFactor, maxFactor);
        client.player.setVelocity(packet.getVelocityX() * factor,
                packet.getVelocityY() * factor,
                packet.getVelocityZ() * factor);
    }

    public void setFactorRange(double min, double max) {
        this.minFactor = min;
        this.maxFactor = max;
    }
}
