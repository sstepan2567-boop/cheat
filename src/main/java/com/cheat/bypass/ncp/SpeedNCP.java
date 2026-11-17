package com.cheat.bypass.ncp;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.TickEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.util.PlayerUtil;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * NCP speed - classic YPort.
 *
 * <p>On every ground contact the player is launched with a small vertical velocity and then
 * pulled down quickly while airborne. The fast fall shortens the air time, so NCP's speed
 * check sees a normal hop while the horizontal momentum is retained.
 */
public final class SpeedNCP extends BypassModule {

    private static final double JUMP_Y = 0.30d;
    private static final double FALL_Y = -0.10d;

    public SpeedNCP() {
        super("SpeedNCP", "NCP speed: classic YPort hop.",
                ModuleCategory.MOVEMENT, Anticheat.NCP);
    }

    @Override
    public void onTick(TickEvent event) {
        ClientPlayerEntity player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (player.isOnGround()) {
            if (PlayerUtil.getHorizontalSpeed(player) > 0.08d) {
                player.setVelocity(player.getVelocity().x, JUMP_Y, player.getVelocity().z);
                player.setOnGround(false);
            }
        } else if (player.getVelocity().y > FALL_Y) {
            player.setVelocity(player.getVelocity().x, FALL_Y, player.getVelocity().z);
        }
    }
}
