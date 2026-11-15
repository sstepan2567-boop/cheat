package com.cheat.movement;

import com.cheat.event.PacketEvent;
import com.cheat.module.Module;
import com.cheat.module.ModuleCategory;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Round-trip latency probe.
 *
 * <p>Every sequenced outgoing packet ({@code PlayerInteractBlockC2SPacket},
 * {@code PlayerActionC2SPacket}) is timestamped by its sequence number. The server answers
 * with {@code PlayerActionResponseS2CPacket}; matching the two gives a real round-trip
 * time that modes use to decide how long a transaction may be delayed.
 */
public final class PingPongModule extends Module {

    private final Map<Integer, Long> sentSequences = new HashMap<>();
    private volatile int latencyMillis;

    public PingPongModule() {
        super("PingPong", "Measures the server round-trip time through block action sequences.",
                ModuleCategory.MISC, true);
    }

    @Override
    public void onPacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();
        if (event.isOutgoing()) {
            int sequence = -1;
            if (packet instanceof PlayerInteractBlockC2SPacket interactBlock) {
                sequence = interactBlock.getSequence();
            } else if (packet instanceof PlayerActionC2SPacket action) {
                sequence = action.getSequence();
            }
            if (sequence >= 0) {
                sentSequences.put(sequence, Util.getMeasuringTimeMs());
            }
        } else if (packet instanceof PlayerActionResponseS2CPacket response) {
            Long sent = sentSequences.remove(response.sequence());
            if (sent != null) {
                latencyMillis = (int) Math.min(Integer.MAX_VALUE, Util.getMeasuringTimeMs() - sent);
            }
        }
    }

    /** Last measured round-trip time in milliseconds, or {@code 0} when nothing was measured. */
    public int getLatencyMillis() {
        return latencyMillis;
    }
}
