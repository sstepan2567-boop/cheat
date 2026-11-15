package com.cheat.bypass.hypixel;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.PacketEvent;
import com.cheat.event.TickEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.movement.PlayerMoveRewriter;
import com.cheat.packet.PacketLayer;
import com.cheat.util.MathUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/**
 * Hypixel NoFall.
 *
 * <p>While the fall distance is above the threshold the mode arms an {@code onGround = true}
 * rewrite for a randomised number of ticks. The random tick offset prevents Watchdog from
 * correlating the ground flag with the fall start.
 */
public final class NoFallHypixel extends BypassModule {

    private static final float FALL_THRESHOLD = 2.0f;

    private int groundTicks;

    public NoFallHypixel() {
        super("NoFallHypixel", "Hypixel NoFall: onGround with a randomised tick offset.",
                ModuleCategory.MOVEMENT, Anticheat.HYPIXEL);
    }

    @Override
    public void onTick(TickEvent event) {
        if (groundTicks > 0) {
            groundTicks--;
        }
        ClientPlayerEntity player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (player.fallDistance >= FALL_THRESHOLD && player.getVelocity().y < 0.0d
                && MathUtil.chance(0.4d)) {
            groundTicks = MathUtil.random(1, 3);
        }
    }

    @Override
    public void onPacket(PacketEvent event) {
        if (!event.isOutgoing() || !(event.getPacket() instanceof PlayerMoveC2SPacket move)) {
            return;
        }
        if (groundTicks > 0 && !move.isOnGround()) {
            event.cancel();
            PacketLayer.sendRaw(client(), PlayerMoveRewriter.withOnGround(move, true));
        }
    }
}
