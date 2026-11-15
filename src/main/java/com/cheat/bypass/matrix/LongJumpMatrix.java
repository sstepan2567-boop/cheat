package com.cheat.bypass.matrix;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.TickEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.util.PlayerUtil;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Matrix long jump - {@code 7.14.5-Flag}.
 *
 * <p>On the ground the mode intentionally overshoots the speed limit to trigger Matrix's
 * flag, then uses the accumulated momentum for a few ticks while damping the Y movement.
 * After the flag window the speed is allowed to decay, which is the pattern Matrix's
 * long-jump check tolerates.
 */
public final class LongJumpMatrix extends BypassModule {

    private static final double OVERSHOOT = 1.45d;
    private static final double LAUNCH_Y = 0.38d;
    private static final int MOMENTUM_TICKS = 3;

    private int momentumTicks;

    public LongJumpMatrix() {
        super("LongJumpMatrix", "Matrix long jump (7.14.5-Flag): intentional flag followed by momentum.",
                ModuleCategory.MOVEMENT, Anticheat.MATRIX);
    }

    @Override
    public void onTick(TickEvent event) {
        ClientPlayerEntity player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (momentumTicks > 0) {
            momentumTicks--;
            player.setVelocity(player.getVelocity().x, 0.0d, player.getVelocity().z);
            return;
        }
        if (player.isOnGround() && PlayerUtil.getHorizontalSpeed(player) > 0.10d) {
            player.setVelocity(player.getVelocity().x * OVERSHOOT, LAUNCH_Y,
                    player.getVelocity().z * OVERSHOOT);
            player.setOnGround(false);
            momentumTicks = MOMENTUM_TICKS;
        }
    }
}
