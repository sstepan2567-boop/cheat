package com.cheat.bypass.ncp;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.TickEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.packet.PacketLayer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;

/**
 * NCP clip flight.
 *
 * <p>Steps the player downward in small increments. When the target position is inside a
 * solid block the player is moved to the top of that block and a
 * {@link PlayerMoveC2SPacket.PositionAndOnGround} with the clipped position is written,
 * so NCP's collision check sees a valid position instead of a vertical teleport.
 */
public final class FlyNcpClip extends BypassModule {

    private static final double STEP = 0.5d;

    public FlyNcpClip() {
        super("FlyNcpClip", "NCP fly: clips through blocks with stepped position packets.",
                ModuleCategory.MOVEMENT, Anticheat.NCP);
    }

    @Override
    public void onTick(TickEvent event) {
        ClientPlayerEntity player = event.getPlayer();
        if (player == null) {
            return;
        }
        double targetY = player.getY() - STEP;
        BlockPos target = BlockPos.ofFloored(player.getX(), targetY, player.getZ());
        if (!player.getWorld().getBlockState(target).isAir()) {
            double clippedY = target.getY() + 1.001d;
            player.setPosition(player.getX(), clippedY, player.getZ());
            player.setVelocity(player.getVelocity().x, 0.0d, player.getVelocity().z);
            PacketLayer.sendRaw(client(), new PlayerMoveC2SPacket.PositionAndOnGround(
                    player.getX(), clippedY, player.getZ(), true, false));
        } else {
            player.setVelocity(player.getVelocity().x, -STEP, player.getVelocity().z);
        }
    }
}
