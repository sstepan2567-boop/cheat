package com.cheat.bypass.vulcan;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.TickEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.module.ModuleManager;
import com.cheat.movement.MicroJump;
import com.cheat.util.PlayerUtil;
import com.cheat.util.TimerUtil;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Vulcan speed.
 *
 * <p>Submode {@code Vulcan286}: micro jump of 0.24 every ground contact with a 1.04x
 * timer compensation. Submode {@code Vulcan288}: micro jump of 0.28 with a 1.08x timer.
 * The timer is only applied while the mode is enabled, so the speed gain stays inside the
 * window Vulcan's speed check tolerates.
 */
public final class SpeedVulcan extends BypassModule {

    public enum Submode {
        VULCAN286(0.24d, 1.04f),
        VULCAN288(0.28d, 1.08f);

        private final double jumpHeight;
        private final float timer;

        Submode(double jumpHeight, float timer) {
            this.jumpHeight = jumpHeight;
            this.timer = timer;
        }
    }

    private final MicroJump microJump;
    private Submode submode = Submode.VULCAN286;

    public SpeedVulcan() {
        super("SpeedVulcan", "Vulcan speed: timer compensation plus micro jumps (286 / 288).",
                ModuleCategory.MOVEMENT, Anticheat.VULCAN);
        this.microJump = ModuleManager.getModule(MicroJump.class);
    }

    @Override
    public void onEnable() {
        TimerUtil.setMultiplier(submode.timer);
    }

    @Override
    public void onDisable() {
        TimerUtil.setMultiplier(1.0f);
    }

    @Override
    public void onTick(TickEvent event) {
        ClientPlayerEntity player = event.getPlayer();
        if (player == null || microJump == null) {
            return;
        }
        if (player.isOnGround() && PlayerUtil.getHorizontalSpeed(player) > 0.10d) {
            microJump.microJump(player, submode.jumpHeight);
        }
    }

    public Submode getSubmode() {
        return submode;
    }

    public void setSubmode(Submode submode) {
        this.submode = submode;
        if (isEnabled()) {
            TimerUtil.setMultiplier(submode.timer);
        }
    }
}
