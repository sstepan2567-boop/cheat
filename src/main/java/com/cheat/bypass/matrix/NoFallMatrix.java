package com.cheat.bypass.matrix;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.PacketEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.movement.PlayerMoveRewriter;
import com.cheat.packet.PacketLayer;
import com.cheat.util.TimerUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Util;

/**
 * Matrix NoFall.
 *
 * <p>Rewrites the outgoing {@link PlayerMoveC2SPacket} with {@code onGround = true} while
 * the fall distance is above the threshold. The rewrite is rate limited by the Matrix
 * timer (at most once every 200 ms) so the ground flag does not toggle faster than the
 * anticheat samples movement.
 */
public final class NoFallMatrix extends BypassModule {

    private static final long REWRITE_INTERVAL_MILLIS = 200L;
    private static final float FALL_THRESHOLD = 2.0f;

    private long lastRewriteMillis;

    public NoFallMatrix() {
        super("NoFallMatrix", "Matrix NoFall: onGround bound to the Matrix timer.",
                ModuleCategory.MOVEMENT, Anticheat.MATRIX);
    }

    @Override
    public void onPacket(PacketEvent event) {
        if (!event.isOutgoing() || !(event.getPacket() instanceof PlayerMoveC2SPacket move)) {
            return;
        }
        ClientPlayerEntity player = client().player;
        if (player == null || player.fallDistance < FALL_THRESHOLD || move.isOnGround()) {
            return;
        }
        long now = Util.getMeasuringTimeMs();
        if (!TimerUtil.isDue(lastRewriteMillis, REWRITE_INTERVAL_MILLIS)) {
            return;
        }
        lastRewriteMillis = now;
        event.cancel();
        PacketLayer.sendRaw(client(), PlayerMoveRewriter.withOnGround(move, true));
    }
}
