package com.cheat.bypass.matrix;

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
 * Matrix speed - timer compensation.
 *
 * <p>Applies a small timer multiplier (1.02) and a 0.25 micro jump on every ground
 * contact. The multiplier is derived from the server tick rate reported through
 * {@code UpdateTickRateS2CPacket}, so the packet timing stays inside the limits Matrix
 * measures while the micro jump adds the actual speed.
 */
public final class SpeedMatrix extends BypassModule {

    private static final double JUMP_HEIGHT = 0.25d;
    private static final float TIMER = 1.02f;

    private final MicroJump microJump;

    public SpeedMatrix() {
        super("SpeedMatrix", "Matrix speed: timer compensation with a micro jump.",
                ModuleCategory.MOVEMENT, Anticheat.MATRIX);
        this.microJump = ModuleManager.getModule(MicroJump.class);
    }

    @Override
    public void onEnable() {
        TimerUtil.setMultiplier(TIMER);
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
            microJump.microJump(player, JUMP_HEIGHT);
        }
    }
}
