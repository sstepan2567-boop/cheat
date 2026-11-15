package com.cheat.bypass.ncp;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.PacketEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.movement.PlayerMoveRewriter;
import com.cheat.packet.PacketLayer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/**
 * NCP NoFall.
 *
 * <p>At the fall peak (high downward velocity, non-zero fall distance) the outgoing
 * {@link PlayerMoveC2SPacket} is rewritten with {@code onGround = true}. Only the peak is
 * rewritten, so the rest of the fall looks normal to NCP.
 */
public final class NoFallNCP extends BypassModule {

    private static final float FALL_THRESHOLD = 1.5f;
    private static final double PEAK_VELOCITY = -0.5d;

    public NoFallNCP() {
        super("NoFallNCP", "NCP NoFall: onGround at the fall peak.",
                ModuleCategory.MOVEMENT, Anticheat.NCP);
    }

    @Override
    public void onPacket(PacketEvent event) {
        if (!event.isOutgoing() || !(event.getPacket() instanceof PlayerMoveC2SPacket move)) {
            return;
        }
        ClientPlayerEntity player = client().player;
        if (player == null || move.isOnGround()) {
            return;
        }
        if (player.fallDistance >= FALL_THRESHOLD && player.getVelocity().y <= PEAK_VELOCITY) {
            event.cancel();
            PacketLayer.sendRaw(client(), PlayerMoveRewriter.withOnGround(move, true));
        }
    }
}
