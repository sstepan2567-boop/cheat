package com.cheat.bypass.hypixel;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.TickEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.module.ModuleManager;
import com.cheat.movement.MicroJump;
import com.cheat.util.MathUtil;
import com.cheat.util.PlayerUtil;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Hypixel speed - bunny hop.
 *
 * <p>Re-applies a randomised micro jump on every ground contact. Watchdog expects a
 * regular hop rhythm; the random height keeps the Y delta inside the tolerated band while
 * the hop itself preserves the sprint speed.
 */
public final class SpeedHypixelBHop extends BypassModule {

    private static final double MIN_JUMP = 0.24d;
    private static final double MAX_JUMP = 0.30d;

    private final MicroJump microJump;

    public SpeedHypixelBHop() {
        super("SpeedHypixelBHop", "Hypixel speed: bunny hop with randomised jump height.",
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
            microJump.microJump(player, MathUtil.random(MIN_JUMP, MAX_JUMP));
        }
    }
}
