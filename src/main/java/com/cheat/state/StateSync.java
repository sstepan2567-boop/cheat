package com.cheat.state;

import net.minecraft.util.Util;

import java.util.HashSet;
import java.util.Set;

/**
 * Tracks server side state that bypass modes need in order to stay in sync:
 * <ul>
 *     <li>teleport transactions ({@code PlayerPositionLookS2CPacket} -&gt;
 *     {@code TeleportConfirmC2SPacket}), the modern replacement for the old
 *     {@code TransactionS2CPacket};</li>
 *     <li>block action sequences ({@code PlayerActionResponseS2CPacket});</li>
 *     <li>the server tick rate and freeze state ({@code UpdateTickRateS2CPacket},
 *     {@code TickStepS2CPacket}).</li>
 * </ul>
 */
public final class StateSync {

    private final Set<Integer> pendingTeleports = new HashSet<>();
    private final Set<Integer> pendingSequences = new HashSet<>();

    private volatile int lastTeleportId = -1;
    private volatile int lastSequence = Integer.MIN_VALUE;
    private volatile float serverTickRate = 20.0f;
    private volatile int tickSteps = 0;
    private volatile long lastTeleportMillis = 0L;

    public void onTeleport(int teleportId) {
        lastTeleportId = teleportId;
        pendingTeleports.add(teleportId);
        lastTeleportMillis = Util.getMeasuringTimeMs();
    }

    public void onTeleportConfirmed(int teleportId) {
        pendingTeleports.remove(teleportId);
    }

    public boolean isTeleportPending(int teleportId) {
        return pendingTeleports.contains(teleportId);
    }

    public boolean hasPendingTeleports() {
        return !pendingTeleports.isEmpty();
    }

    public int getLastTeleportId() {
        return lastTeleportId;
    }

    public void trackSequence(int sequence) {
        pendingSequences.add(sequence);
    }

    public void onSequence(int sequence) {
        lastSequence = sequence;
        pendingSequences.remove(sequence);
    }

    public boolean isSequencePending(int sequence) {
        return pendingSequences.contains(sequence);
    }

    public int getLastSequence() {
        return lastSequence;
    }

    public void setServerTickRate(float tickRate) {
        if (tickRate > 0.0f) {
            serverTickRate = tickRate;
        }
    }

    public float getServerTickRate() {
        return serverTickRate;
    }

    public void setTickSteps(int tickSteps) {
        this.tickSteps = tickSteps;
    }

    public int getTickSteps() {
        return tickSteps;
    }

    public boolean isServerFrozen() {
        return tickSteps > 0;
    }

    /** Milliseconds elapsed since the last teleport request. */
    public long getTeleportElapsedMillis() {
        return Util.getMeasuringTimeMs() - lastTeleportMillis;
    }

    /**
     * Ratio between the server tick rate and the canonical 20 TPS. Values above 1 mean the
     * server is running slower than normal (frozen or slowed down).
     */
    public float getTimerCompensation() {
        return 20.0f / Math.max(1.0f, serverTickRate);
    }
}
