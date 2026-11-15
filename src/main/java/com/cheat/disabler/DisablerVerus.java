package com.cheat.disabler;

import com.cheat.CheatClient;
import com.cheat.bypass.Anticheat;
import com.cheat.event.PacketEvent;
import com.cheat.module.ModuleManager;
import com.cheat.movement.PacketQueueModule;
import com.cheat.packet.PacketLayer;
import com.cheat.util.TimerUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

/**
 * Verus disabler profile.
 *
 * <p>Delays the teleport confirmation so Verus's teleport transaction is never answered in
 * the expected window, substitutes the ping round-trip by delaying the block-action
 * acknowledgement, and keeps the sprint state in sync with the server timer.
 */
public final class DisablerVerus implements DisablerProfile {

    private static final long TELEPORT_DELAY_MILLIS = 25L;
    private static final long ATTACK_DELAY_MILLIS = 18L;

    @Override
    public Anticheat getAnticheat() {
        return Anticheat.VERUS;
    }

    @Override
    public boolean applyC2S(PacketEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof TeleportConfirmC2SPacket confirm) {
            ModuleManager.getModule(PacketQueueModule.class).sendDelayed(confirm, TELEPORT_DELAY_MILLIS);
            return true;
        }
        if (packet instanceof PlayerInteractEntityC2SPacket) {
            ModuleManager.getModule(PacketQueueModule.class).sendDelayed(packet, ATTACK_DELAY_MILLIS);
            return true;
        }
        return false;
    }

    @Override
    public void onS2C(PacketEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof PlayerPositionLookS2CPacket teleport) {
            CheatClient.getInstance().getStateSync().onTeleport(teleport.teleportId());
        } else if (packet instanceof PlayerActionResponseS2CPacket response) {
            CheatClient.getInstance().getStateSync().onSequence(response.sequence());
        }
    }

    @Override
    public void onEnable() {
        TimerUtil.setMultiplier(1.03f);
    }

    @Override
    public void onDisable() {
        TimerUtil.setMultiplier(1.0f);
    }
}
