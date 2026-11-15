package com.cheat.bypass.hypixel;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.TickEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.module.ModuleManager;
import com.cheat.movement.MicroJump;
import com.cheat.util.PlayerUtil;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Hypixel speed - low hop.
 *
 * <p>A low, fixed micro jump on every ground contact. The small Y delta stays below the
 * threshold Watchdog uses to separate a hop from a flight attempt.
 */
public final class SpeedHypixelLowHop extends BypassModule {

    private static final double JUMP_HEIGHT = 0.18d;

    private final MicroJump microJump;

    public SpeedHypixelLowHop() {
        super("SpeedHypixelLowHop", "Hypixel speed: low bunny hop.",
                ModuleCategory.MOVEMENT, Anticheat.HYPIXEL);
        this.microJump = ModuleManager.getModule(MicroJump.class);
    }

    @Override
    public void onTick(TickEvent event) {
        ClientPlayerEntity player = event.getPlayer();
        if (player == null || microJump == null) {
            return;
        }
        if (player.isOnGround() && PlayerUtil.getHorizontalSpeed(player) > 0.10d) {
            microJump.microJump(player, JUMP_HEIGHT);
        }
    }
}
