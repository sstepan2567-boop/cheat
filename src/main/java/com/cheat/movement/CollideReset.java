package com.cheat.movement;

import com.cheat.event.TickEvent;
import com.cheat.module.Module;
import com.cheat.module.ModuleCategory;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

/**
 * Base collision reset shared by the Grim speed modes.
 *
 * <p>When the player hits a block while airborne the horizontal momentum is cancelled and
 * a small downward nudge is applied, which makes the server see a collision reset instead
 * of a speed gain. {@link #resetAtApex(ClientPlayerEntity)} is the variant used at the top
 * of a jump, where no block has been touched yet.
 */
public final class CollideReset extends Module {

    private static final double APEX_RETENTION = 0.10;
    private static final double APEX_DOWNWARD = -0.08;

    private int cooldown;

    public CollideReset() {
        super("CollideReset", "Resets horizontal velocity through a block collision at the jump apex.",
                ModuleCategory.MOVEMENT);
    }

    @Override
    public void onTick(TickEvent event) {
        if (cooldown > 0) {
            cooldown--;
        }
        ClientPlayerEntity player = event.getPlayer();
        if (player == null || cooldown > 0) {
            return;
        }
        if (!player.isOnGround() && player.horizontalCollision) {
            resetAtApex(player);
            cooldown = 2;
        }
    }

    /**
     * Cancels most of the horizontal velocity and pushes the player slightly down so the
     * next {@code PlayerMoveC2SPacket} reports a position consistent with a collision.
     */
    public void resetAtApex(ClientPlayerEntity player) {
        Vec3d velocity = player.getVelocity();
        player.setVelocity(velocity.x * APEX_RETENTION, APEX_DOWNWARD, velocity.z * APEX_RETENTION);
    }
}
