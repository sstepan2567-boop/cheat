package com.cheat.bypass.verus;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.PacketEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.packet.PacketLayer;
import com.cheat.util.MathUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;

/**
 * Verus combat.
 *
 * <p>Delays the teleport confirmation and the block-action acknowledgement, syncs
 * {@code START_SPRINTING} before the attack and jitters the attack itself. Verus compares
 * the sprint state, the transaction order and the attack timing; rewriting all three keeps
 * the hit inside the expected window.
 */
public final class CombatVerus extends BypassModule {

    private long teleportDelayMillis = 12L;
    private long attackDelayMinMillis = 10L;
    private long attackDelayMaxMillis = 30L;

    public CombatVerus() {
        super("CombatVerus", "Verus combat: transaction delay, sprint sync and packet order before the attack.",
                ModuleCategory.COMBAT, Anticheat.VERUS);
    }

    @Override
    public void onPacket(PacketEvent event) {
        if (event.isOutgoing() && event.getPacket() instanceof PlayerInteractEntityC2SPacket) {
            ClientPlayerEntity player = client().player;
            if (player == null) {
                return;
            }
            event.cancel();
            PacketLayer.sendRaw(client(), new ClientCommandC2SPacket(player,
                    ClientCommandC2SPacket.Mode.START_SPRINTING));
            queue().sendDelayed(event.getPacket(),
                    (long) MathUtil.random(attackDelayMinMillis, attackDelayMaxMillis));
        } else if (event.isOutgoing() && event.getPacket() instanceof TeleportConfirmC2SPacket confirm) {
            event.cancel();
            queue().sendDelayed(confirm, teleportDelayMillis);
        } else if (event.isIncoming() && event.getPacket() instanceof PlayerActionResponseS2CPacket response) {
            state().onSequence(response.sequence());
        }
    }

    public void setDelays(long teleportDelay, long attackMin, long attackMax) {
        this.teleportDelayMillis = teleportDelay;
        this.attackDelayMinMillis = attackMin;
        this.attackDelayMaxMillis = attackMax;
    }
}
