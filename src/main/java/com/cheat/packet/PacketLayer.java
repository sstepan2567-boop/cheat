package com.cheat.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;

/**
 * Bridge between the packet mixins and the module system.
 *
 * <p>The mixins post a {@link com.cheat.event.PacketEvent} for every packet and cancel it
 * when a module asks for it. Modules that need to replace a packet (instead of just
 * dropping it) cancel the original and send the replacement through
 * {@link #sendRaw(MinecraftClient, Packet)}, which bypasses the outgoing mixin so the
 * replacement is written exactly once.
 */
public final class PacketLayer {

    private static final ThreadLocal<Boolean> SUPPRESSED = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private PacketLayer() {
    }

    public static boolean isSuppressed() {
        return SUPPRESSED.get();
    }

    /**
     * Sends a packet straight through the network handler, skipping the outgoing event.
     * Used by modules that rewrite an outgoing packet.
     */
    public static void sendRaw(MinecraftClient client, Packet<?> packet) {
        if (client == null || client.getNetworkHandler() == null) {
            return;
        }
        SUPPRESSED.set(true);
        try {
            client.getNetworkHandler().sendPacket(packet);
        } finally {
            SUPPRESSED.set(false);
        }
    }
}
