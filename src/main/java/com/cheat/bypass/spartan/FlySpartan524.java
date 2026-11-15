package com.cheat.bypass.spartan;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.TickEvent;
import com.cheat.module.ModuleCategory;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Spartan fly - {@code 5.2.4}.
 *
 * <p>Keeps the player at a low altitude with a small constant vertical step. The step is
 * well below Spartan's flight flag threshold, so the mode can hold a position in the air
 * without triggering the check.
 */
public final class FlySpartan524 extends BypassModule {

    private static final double VERTICAL_STEP = 0.08d;

    public FlySpartan524() {
        super("FlySpartan524", "Spartan fly (5.2.4): low altitude, small vertical steps.",
                ModuleCategory.MOVEMENT, Anticheat.SPARTAN);
    }

    @Override
    public void onTick(TickEvent event) {
        ClientPlayerEntity player = event.getPlayer();
        if (player == null) {
            return;
        }
        player.setVelocity(player.getVelocity().x, VERTICAL_STEP, player.getVelocity().z);
        player.setOnGround(false);
    }
}
