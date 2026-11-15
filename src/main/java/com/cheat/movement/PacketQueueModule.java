package com.cheat.movement;

import com.cheat.event.TickEvent;
import com.cheat.module.Module;
import com.cheat.module.ModuleCategory;
import com.cheat.packet.PacketLayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Delayed packet queue. Modes that need to reorder or delay packets (Scaffold, Combat,
 * Disabler) push the packet here instead of writing it immediately; the queue is flushed
 * on every client tick.
 */
public final class PacketQueueModule extends Module {

    private final List<QueuedPacket> queue = new ArrayList<>();

    public PacketQueueModule() {
        super("PacketQueue", "Buffers outgoing packets and releases them after a configurable delay.",
                ModuleCategory.MOVEMENT, true);
    }

    @Override
    public void onTick(TickEvent event) {
        if (queue.isEmpty()) {
            return;
        }
        long now = Util.getMeasuringTimeMs();
        Iterator<QueuedPacket> iterator = queue.iterator();
        while (iterator.hasNext()) {
            QueuedPacket queued = iterator.next();
            if (now >= queued.dueMillis()) {
                PacketLayer.sendRaw(event.getClient(), queued.packet());
                iterator.remove();
            }
        }
    }

    /** Adds a packet to the queue with a delay in milliseconds. */
    public void sendDelayed(Packet<?> packet, long delayMillis) {
        queue.add(new QueuedPacket(packet, Util.getMeasuringTimeMs() + Math.max(0, delayMillis)));
    }

    /** Adds a packet to the queue with an absolute due time. */
    public void sendAt(Packet<?> packet, long dueMillis) {
        queue.add(new QueuedPacket(packet, dueMillis));
    }

    public int size() {
        return queue.size();
    }

    public void clear() {
        queue.clear();
    }

    private record QueuedPacket(Packet<?> packet, long dueMillis) {
    }
}
