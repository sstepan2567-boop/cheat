package com.cheat.bypass.spartan;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.PacketEvent;
import com.cheat.event.TickEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.movement.PlayerMoveRewriter;
import com.cheat.packet.PacketLayer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/**
 * Spartan NoFall.
 *
 * <p>Arms a two-tick {@code onGround = true} rewrite once the fall distance passes the
 * threshold. The rewrite window is short so Spartan sees a normal fall with a single
 * corrected ground sample.
 */
public final class NoFallSpartan extends BypassModule {

    private static final float FALL_THRESHOLD = 2.5f;

    private int groundTicks;

    public NoFallSpartan() {
        super("NoFallSpartan", "Spartan NoFall: onGround with fall control.",
                ModuleCategory.MOVEMENT, Anticheat.SPARTAN);
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
        if (player.fallDistance >= FALL_THRESHOLD && player.getVelocity().y < 0.0d) {
            groundTicks = 2;
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
