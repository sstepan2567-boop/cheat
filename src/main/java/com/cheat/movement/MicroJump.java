package com.cheat.movement;

import com.cheat.event.TickEvent;
import com.cheat.module.Module;
import com.cheat.module.ModuleCategory;
import com.cheat.util.PlayerUtil;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Small, controlled jump used by the speed modes.
 *
 * <p>Unlike {@code player.jump()} a micro jump uses a configurable vertical velocity so the
 * mode can keep the Y delta inside the limit the anticheat expects. The module itself
 * re-applies the jump every {@link #interval} ticks while the player is running on ground.
 */
public final class MicroJump extends Module {

    private double height = 0.26d;
    private int interval = 1;
    private int timer;

    public MicroJump() {
        super("MicroJump", "Controlled low jump used to build speed without tripping ground checks.",
                ModuleCategory.MOVEMENT);
    }

    @Override
    public void onTick(TickEvent event) {
        if (timer > 0) {
            timer--;
        }
        ClientPlayerEntity player = event.getPlayer();
        if (player == null || timer > 0) {
            return;
        }
        if (player.isOnGround() && PlayerUtil.getHorizontalSpeed(player) > 0.08d) {
            microJump(player, height);
            timer = interval;
        }
    }

    /** Applies a jump with an explicit vertical velocity. */
    public void microJump(ClientPlayerEntity player, double jumpHeight) {
        player.setVelocity(player.getVelocity().x, jumpHeight, player.getVelocity().z);
        player.setOnGround(false);
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = Math.max(1, interval);
    }
}
