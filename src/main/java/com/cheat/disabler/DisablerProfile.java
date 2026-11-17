package com.cheat.disabler;

import com.cheat.bypass.Anticheat;
import com.cheat.event.PacketEvent;

/**
 * Per-anticheat disabler profile.
 *
 * <p>{@link #applyC2S(PacketEvent)} inspects an outgoing packet and returns {@code true}
 * when the packet must be dropped. {@link #onS2C(PacketEvent)} inspects an incoming packet
 * and updates the shared {@link com.cheat.state.StateSync} state.
 */
public interface DisablerProfile {

    Anticheat getAnticheat();

    boolean applyC2S(PacketEvent event);

    void onS2C(PacketEvent event);

    default void onEnable() {
    }

    default void onDisable() {
    }
}
