package com.cheat.movement;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/**
 * Rebuilds {@link PlayerMoveC2SPacket}s with a modified {@code onGround} flag.
 *
 * <p>The packet subclasses are immutable, so a NoFall mode cancels the original packet and
 * sends the rewritten copy through {@link com.cheat.packet.PacketLayer#sendRaw}. The
 * rewritten packet keeps the exact same kind (full / position / look / ground-only) and the
 * exact same coordinates, so the server cannot tell the difference except for the flag.
 */
public final class PlayerMoveRewriter {

    private PlayerMoveRewriter() {
    }

    public static PlayerMoveC2SPacket withOnGround(PlayerMoveC2SPacket packet, boolean onGround) {
        if (packet instanceof PlayerMoveC2SPacket.Full full) {
            return new PlayerMoveC2SPacket.Full(full.getX(0.0), full.getY(0.0), full.getZ(0.0),
                    full.getYaw(0.0f), full.getPitch(0.0f), onGround, full.horizontalCollision());
        }
        if (packet instanceof PlayerMoveC2SPacket.PositionAndOnGround position) {
            return new PlayerMoveC2SPacket.PositionAndOnGround(position.getX(0.0), position.getY(0.0),
                    position.getZ(0.0), onGround, position.horizontalCollision());
        }
        if (packet instanceof PlayerMoveC2SPacket.LookAndOnGround look) {
            return new PlayerMoveC2SPacket.LookAndOnGround(look.getYaw(0.0f), look.getPitch(0.0f),
                    onGround, look.horizontalCollision());
        }
        if (packet instanceof PlayerMoveC2SPacket.OnGroundOnly groundOnly) {
            return new PlayerMoveC2SPacket.OnGroundOnly(onGround, groundOnly.horizontalCollision());
        }
        return packet;
    }

    /** Returns the on-ground flag the packet currently carries. */
    public static boolean isOnGround(PlayerMoveC2SPacket packet) {
        return packet.isOnGround();
    }
}
