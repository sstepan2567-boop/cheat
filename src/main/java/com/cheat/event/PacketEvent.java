package com.cheat.event;

import net.minecraft.network.packet.Packet;

/**
 * Posted for every packet that crosses the client boundary.
 *
 * <p>Outgoing packets are posted from a mixin on {@code ClientCommonNetworkHandler#sendPacket};
 * incoming packets are posted from mixins on the {@code ClientPlayNetworkHandler} handler methods.
 * Cancelling an outgoing packet stops it from being written to the connection, cancelling an
 * incoming packet stops vanilla from processing it.
 */
public final class PacketEvent extends Event {

    public enum Direction {
        OUTGOING,
        INCOMING
    }

    private final Packet<?> packet;
    private final Direction direction;

    public PacketEvent(Packet<?> packet, Direction direction) {
        this.packet = packet;
        this.direction = direction;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    @SuppressWarnings("unchecked")
    public <T extends Packet<?>> T getPacket(Class<T> type) {
        return (T) packet;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isOutgoing() {
        return direction == Direction.OUTGOING;
    }

    public boolean isIncoming() {
        return direction == Direction.INCOMING;
    }
}
