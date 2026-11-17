package com.cheat.bypass.grim;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.PacketEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.util.CombatContext;
import com.cheat.util.MathUtil;
import com.cheat.util.PlayerUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

/**
 * Grim reach calibration.
 *
 * <p>Before an attack packet is written, the distance to the recorded target
 * ({@link CombatContext}) is compared with a randomised reach in {@code [3.00, 3.01]}.
 * Packets that exceed the limit are cancelled, which keeps Grim's reach check inside the
 * expected envelope while still allowing slightly different distances every hit.
 */
public final class ReachGrim extends BypassModule {

    private double minReach = 3.00d;
    private double maxReach = 3.01d;

    public ReachGrim() {
        super("ReachGrim", "Grim reach: clamps attacks to a randomised 3.00-3.01 reach.",
                ModuleCategory.COMBAT, Anticheat.GRIM);
    }

    @Override
    public void onPacket(PacketEvent event) {
        if (!event.isOutgoing() || !(event.getPacket() instanceof PlayerInteractEntityC2SPacket)) {
            return;
        }
        if (!CombatContext.hasFreshTarget()) {
            return;
        }
        Entity target = CombatContext.getTarget();
        ClientPlayerEntity player = client().player;
        if (player == null || target == null) {
            return;
        }
        double distance = PlayerUtil.distanceTo(player, target);
        double reach = MathUtil.random(minReach, maxReach);
        if (distance > reach) {
            event.cancel();
        }
    }

    public void setReachRange(double min, double max) {
        this.minReach = min;
        this.maxReach = max;
    }
}
