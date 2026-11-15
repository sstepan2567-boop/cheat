package com.cheat.util;

import com.cheat.state.StateSync;

/**
 * Timer / pacing helpers shared by the bypass modes.
 *
 * <p>The server tick rate is reported by {@code UpdateTickRateS2CPacket} and the freeze
 * state by {@code TickStepS2CPacket}. Modes use {@link #scale(float, StateSync)} to keep
 * their movement steps proportional to the rate the server actually simulates, and
 * {@link #isDue(long, long)} to pace packet bursts without exceeding vanilla limits.
 */
public final class TimerUtil {

    private static volatile float multiplier = 1.0f;

    private TimerUtil() {
    }

    public static float getMultiplier() {
        return multiplier;
    }

    public static void setMultiplier(float value) {
        multiplier = (float) MathUtil.clamp(value, 0.1d, 5.0d);
    }

    /** Scales a movement value by the server timer compensation and the user multiplier. */
    public static float scale(float value, StateSync sync) {
        float compensation = (float) MathUtil.clamp(sync.getTimerCompensation(), 0.5d, 2.0d);
        return value * compensation * multiplier;
    }

    /** True when at least {@code intervalMillis} have passed since {@code lastMillis}. */
    public static boolean isDue(long lastMillis, long intervalMillis) {
        return net.minecraft.util.Util.getMeasuringTimeMs() - lastMillis >= intervalMillis;
    }

    /**
     * Number of client ticks that correspond to the server freeze requested through
     * {@code TickStepS2CPacket}. Zero when the server is running normally.
     */
    public static int frozenTicks(StateSync sync) {
        return Math.max(0, sync.getTickSteps());
    }
}
