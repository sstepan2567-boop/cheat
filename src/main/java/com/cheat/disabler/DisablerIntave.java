package com.cheat.disabler;

import com.cheat.CheatClient;
import com.cheat.bypass.Anticheat;
import com.cheat.event.PacketEvent;
import com.cheat.module.ModuleManager;
import com.cheat.movement.PacketQueueModule;
import com.cheat.util.MathUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

/**
 * Intave disabler profile.
 *
 * <p>Delays every outgoing movement packet by a randomised amount while a teleport
 * transaction is outstanding, and feeds the transaction id back into
 * {@link com.cheat.state.StateSync}. Intave uses the transaction order to detect speed,
 * so the desynchronisation disables the check.
 */
public final class DisablerIntave implements DisablerProfile {

    @Override
    public Anticheat getAnticheat() {
        return Anticheat.INTAVE;
    }

    @Override
    public boolean applyC2S(PacketEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof PlayerMoveC2SPacket move
                && CheatClient.getInstance().getStateSync().hasPendingTeleports()
                && !move.isOnGround()) {
            ModuleManager.getModule(PacketQueueModule.class)
                    .sendDelayed(move, (long) MathUtil.random(10L, 30L));
            return true;
        }
        return false;
    }

    @Override
    public void onS2C(PacketEvent event) {
        if (event.getPacket() instanceof PlayerPositionLookS2CPacket teleport) {
            CheatClient.getInstance().getStateSync().onTeleport(teleport.teleportId());
        }
    }
}
