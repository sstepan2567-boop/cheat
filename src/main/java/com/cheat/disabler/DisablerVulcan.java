package com.cheat.disabler;

import com.cheat.CheatClient;
import com.cheat.bypass.Anticheat;
import com.cheat.event.PacketEvent;
import com.cheat.module.ModuleManager;
import com.cheat.movement.PacketQueueModule;
import com.cheat.packet.PacketLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

/**
 * Vulcan disabler profile.
 *
 * <p>Delays the teleport confirmation (substituting the ping round-trip Vulcan measures)
 * and holds the attack packet until the transaction is resolved.
 */
public final class DisablerVulcan implements DisablerProfile {

    private static final long TELEPORT_DELAY_MILLIS = 20L;
    private static final long ATTACK_DELAY_MILLIS = 15L;

    @Override
    public Anticheat getAnticheat() {
        return Anticheat.VULCAN;
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
}
