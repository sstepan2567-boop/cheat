package com.cheat.util;

import net.minecraft.entity.Entity;

/**
 * Remembers the entity the player is currently attacking.
 *
 * <p>{@code PlayerInteractEntityC2SPacket} does not expose its entity id, so the combat
 * modes that need the target (reach calibration, combat delays) read it from here. The
 * value is recorded by {@code ClientPlayerInteractionManagerMixin} right before the attack
 * packet is written.
 */
public final class CombatContext {

    private static Entity target;
    private static long recordedMillis;

    private CombatContext() {
    }

    public static void record(Entity entity) {
        target = entity;
        recordedMillis = net.minecraft.util.Util.getMeasuringTimeMs();
    }

    public static Entity getTarget() {
        return target;
    }

    /** True when the recorded target is fresh enough to belong to the packet in flight. */
    public static boolean hasFreshTarget() {
        return target != null
                && net.minecraft.util.Util.getMeasuringTimeMs() - recordedMillis < 100L;
    }
}
