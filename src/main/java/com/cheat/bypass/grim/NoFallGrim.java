package com.cheat.bypass.grim;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.PacketEvent;
import com.cheat.event.TickEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.movement.PlayerMoveRewriter;
import com.cheat.packet.PacketLayer;
import com.cheat.util.PlayerUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/**
 * Grim NoFall.
 *
 * <p>Once the player has been falling far enough, the next outgoing
 * {@link PlayerMoveC2SPacket} is rewritten with {@code onGround = true}. The rewrite is
 * armed from a tick that is bound to the last teleport transaction, so the server sees the
 * ground flag change in the same transaction window as the fall.
 */
public final class NoFallGrim extends BypassModule {

    private float fallThreshold = 2.0f;
    private int groundTicks;

    public NoFallGrim() {
        super("NoFallGrim", "Grim NoFall: reports onGround at the fall peak, synced with the teleport transaction.",
                ModuleCategory.MOVEMENT, Anticheat.GRIM);
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
        boolean armed = state().getLastTeleportId() >= 0;
        if (armed && PlayerUtil.isFalling(player) && player.fallDistance >= fallThreshold) {
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

    public void setFallThreshold(float threshold) {
        this.fallThreshold = threshold;
    }
}
