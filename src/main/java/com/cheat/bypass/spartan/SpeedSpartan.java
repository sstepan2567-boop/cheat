package com.cheat.bypass.spartan;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.TickEvent;
import com.cheat.module.ModuleCategory;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Spartan speed - fall damage boost ({@code 4.0.4.3}).
 *
 * <p>While the player is airborne the accumulated fall distance is converted into a small
 * pending boost. On landing the boost is applied to the horizontal velocity. Spartan's
 * speed check samples the landing speed, so the boost is capped at a fraction of the fall
 * distance to stay inside the tolerated window.
 */
public final class SpeedSpartan extends BypassModule {

    private static final double BOOST_PER_FALL = 0.05d;
    private static final double MAX_BOOST = 0.50d;

    private double pendingBoost;

    public SpeedSpartan() {
        super("SpeedSpartan", "Spartan speed: fall damage boost (4.0.4.3).",
                ModuleCategory.MOVEMENT, Anticheat.SPARTAN);
    }

    @Override
    public void onTick(TickEvent event) {
        ClientPlayerEntity player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (player.isOnGround()) {
            if (pendingBoost > 0.0d) {
                double factor = 1.0d + pendingBoost;
                player.setVelocity(player.getVelocity().x * factor, player.getVelocity().y,
                        player.getVelocity().z * factor);
                pendingBoost = 0.0d;
            }
        } else if (player.fallDistance > 2.0f) {
            pendingBoost = Math.min(MAX_BOOST, player.fallDistance * BOOST_PER_FALL);
        }
    }
}
