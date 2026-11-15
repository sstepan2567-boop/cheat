package com.cheat.bypass.vulcan;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.PacketEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.movement.PlayerMoveRewriter;
import com.cheat.packet.PacketLayer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/**
 * Vulcan NoFall.
 *
 * <p>Rewrites the outgoing {@link PlayerMoveC2SPacket} with {@code onGround = true} only
 * while the fall distance is above the threshold. Unlike the Grim variant this mode does
 * not wait for a transaction, it simply suppresses the ground flag change below the
 * threshold so Vulcan's fall check never sees a full fall.
 */
public final class NoFallVulcan extends BypassModule {

    private float fallThreshold = 3.0f;

    public NoFallVulcan() {
        super("NoFallVulcan", "Vulcan NoFall: onGround only while the fall distance is above the threshold.",
                ModuleCategory.MOVEMENT, Anticheat.VULCAN);
    }

    @Override
    public void onPacket(PacketEvent event) {
        if (!event.isOutgoing() || !(event.getPacket() instanceof PlayerMoveC2SPacket move)) {
            return;
        }
        ClientPlayerEntity player = client().player;
        if (player == null) {
            return;
        }
        if (player.fallDistance >= fallThreshold && !move.isOnGround()) {
            event.cancel();
            PacketLayer.sendRaw(client(), PlayerMoveRewriter.withOnGround(move, true));
        }
    }

    public void setFallThreshold(float threshold) {
        this.fallThreshold = threshold;
    }
}
