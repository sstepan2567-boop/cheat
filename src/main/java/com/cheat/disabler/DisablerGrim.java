package com.cheat.disabler;

import com.cheat.CheatClient;
import com.cheat.bypass.Anticheat;
import com.cheat.event.PacketEvent;
import com.cheat.module.ModuleManager;
import com.cheat.movement.PacketQueueModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

/**
 * Grim disabler profile.
 *
 * <p>Synchronises the teleport transaction id and controls the packet order: the sprint
 * state change is queued ahead of the block place and the movement packet is held back
 * while a transaction is outstanding.
 */
public final class DisablerGrim implements DisablerProfile {

    @Override
    public Anticheat getAnticheat() {
        return Anticheat.GRIM;
    }

    @Override
    public boolean applyC2S(PacketEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof PlayerMoveC2SPacket move
                && CheatClient.getInstance().getStateSync().hasPendingTeleports()
                && !move.isOnGround()) {
            ModuleManager.getModule(PacketQueueModule.class).sendDelayed(move, 12L);
            return true;
        }
        if (packet instanceof PlayerInteractBlockC2SPacket) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                ModuleManager.getModule(PacketQueueModule.class).sendDelayed(
                        new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.START_SPRINTING), 0L);
            }
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
