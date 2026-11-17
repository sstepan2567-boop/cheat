package com.cheat.bypass.hypixel;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.TickEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.util.MathUtil;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Hypixel fly - flat.
 *
 * <p>Holds the player at a near-constant altitude with a randomised vertical step in
 * {@code [-0.04, 0.04]}. Watchdog flags a sustained vertical velocity; the random sign
 * changes keep the average vertical movement close to zero.
 */
public final class FlyHypixelFlat extends BypassModule {

    private static final double MAX_STEP = 0.04d;

    public FlyHypixelFlat() {
        super("FlyHypixelFlat", "Hypixel fly: flat low flight with randomised vertical step.",
                ModuleCategory.MOVEMENT, Anticheat.HYPIXEL);
    }

    @Override
    public void onTick(TickEvent event) {
        ClientPlayerEntity player = event.getPlayer();
        if (player == null) {
            return;
        }
        double step = MathUtil.random(-MAX_STEP, MAX_STEP);
        player.setVelocity(player.getVelocity().x, step, player.getVelocity().z);
        player.setOnGround(false);
    }
}
