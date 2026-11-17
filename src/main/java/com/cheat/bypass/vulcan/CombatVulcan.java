package com.cheat.bypass.vulcan;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.PacketEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.packet.PacketLayer;
import com.cheat.util.MathUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

/**
 * Vulcan combat.
 *
 * <p>Before an attack packet, {@code START_SPRINTING} is written and the attack itself is
 * queued for a short randomised delay. Vulcan's combat check compares the sprint state and
 * the attack timing, so syncing the sprint first and jittering the delay keeps the hit
 * inside the expected window.
 */
public final class CombatVulcan extends BypassModule {

    private long minDelayMillis = 5L;
    private long maxDelayMillis = 15L;

    public CombatVulcan() {
        super("CombatVulcan", "Vulcan combat: sprint sync and transaction delay before the attack.",
                ModuleCategory.COMBAT, Anticheat.VULCAN);
    }

    @Override
    public void onPacket(PacketEvent event) {
        if (!event.isOutgoing() || !(event.getPacket() instanceof PlayerInteractEntityC2SPacket)) {
            return;
        }
        ClientPlayerEntity player = client().player;
        if (player == null) {
            return;
        }
        event.cancel();
        PacketLayer.sendRaw(client(), new ClientCommandC2SPacket(player,
                ClientCommandC2SPacket.Mode.START_SPRINTING));
        queue().sendDelayed(event.getPacket(), (long) MathUtil.random(minDelayMillis, maxDelayMillis));
    }

    public void setDelayRange(long min, long max) {
        this.minDelayMillis = min;
        this.maxDelayMillis = max;
    }
}
