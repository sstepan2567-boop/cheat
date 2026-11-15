package com.cheat.bypass.verus;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.PacketEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.packet.PacketLayer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

/**
 * Verus scaffold.
 *
 * <p>Rewrites the outgoing {@link PlayerInteractBlockC2SPacket}: the {@link BlockHitResult}
 * is rebuilt with the placement position offset by the hit side (so the server sees a
 * normal adjacent placement), the hand is preserved and the sequence is kept. A rotation
 * packet is written before the place, which is the order Verus expects.
 */
public final class ScaffoldVerus extends BypassModule {

    public ScaffoldVerus() {
        super("ScaffoldVerus", "Verus scaffold: rewrites the block hit result, rotation before place.",
                ModuleCategory.MOVEMENT, Anticheat.VERUS);
    }

    @Override
    public void onPacket(PacketEvent event) {
        if (!event.isOutgoing() || !(event.getPacket() instanceof PlayerInteractBlockC2SPacket packet)) {
            return;
        }
        ClientPlayerEntity player = client().player;
        if (player == null) {
            return;
        }
        event.cancel();

        BlockHitResult hit = packet.getBlockHitResult();
        BlockPos placePos = hit.getBlockPos().offset(hit.getSide());
        BlockHitResult rewritten = new BlockHitResult(hit.getPos(), hit.getSide(), placePos,
                hit.isInsideBlock());

        PacketLayer.sendRaw(client(), new PlayerMoveC2SPacket.LookAndOnGround(
                player.getYaw(), player.getPitch(), player.isOnGround(), player.horizontalCollision));
        PacketLayer.sendRaw(client(), new PlayerInteractBlockC2SPacket(packet.getHand(),
                rewritten, packet.getSequence()));
    }
}
