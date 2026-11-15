package com.cheat.bypass.intave;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.PacketEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.util.MathUtil;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

/**
 * Intave combat.
 *
 * <p>Delays the outgoing {@link PlayerInteractEntityC2SPacket} by a randomised amount.
 * Intave measures the time between the swing animation and the attack packet; the jitter
 * keeps that delta inside the expected range.
 */
public final class CombatIntave extends BypassModule {

    private long minDelayMillis = 8L;
    private long maxDelayMillis = 20L;

    public CombatIntave() {
        super("CombatIntave", "Intave combat: randomised delay before the attack packet.",
                ModuleCategory.COMBAT, Anticheat.INTAVE);
    }

    @Override
    public void onPacket(PacketEvent event) {
        if (!event.isOutgoing() || !(event.getPacket() instanceof PlayerInteractEntityC2SPacket)) {
            return;
        }
        event.cancel();
        queue().sendDelayed(event.getPacket(), (long) MathUtil.random(minDelayMillis, maxDelayMillis));
    }

    public void setDelayRange(long min, long max) {
        this.minDelayMillis = min;
        this.maxDelayMillis = max;
    }
}
