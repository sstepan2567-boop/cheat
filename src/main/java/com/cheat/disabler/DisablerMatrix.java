package com.cheat.disabler;

import com.cheat.CheatClient;
import com.cheat.bypass.Anticheat;
import com.cheat.event.PacketEvent;
import com.cheat.module.ModuleManager;
import com.cheat.movement.PacketQueueModule;
import com.cheat.util.TimerUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/**
 * Matrix disabler profile - timer compensation.
 *
 * <p>Matrix measures the time between movement packets. When the server tick rate differs
 * from the canonical 20 TPS the profile delays the outgoing movement packets by the
 * difference, so the packet cadence the server observes matches the timer it expects.
 */
public final class DisablerMatrix implements DisablerProfile {

    private static final float TIMER = 1.0f;

    @Override
    public Anticheat getAnticheat() {
        return Anticheat.MATRIX;
    }

    @Override
    public boolean applyC2S(PacketEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof PlayerMoveC2SPacket) {
            float rate = CheatClient.getInstance().getStateSync().getServerTickRate();
            long delay = Math.round((20.0f / Math.max(1.0f, rate) - 1.0f) * 50.0f);
            if (delay > 0L) {
                ModuleManager.getModule(PacketQueueModule.class).sendDelayed(packet, delay);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onS2C(PacketEvent event) {
        // Matrix reports its state through the tick rate; nothing to rewrite on the way in.
    }

    @Override
    public void onEnable() {
        TimerUtil.setMultiplier(TIMER);
    }
}
