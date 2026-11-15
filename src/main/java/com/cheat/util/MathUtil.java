package com.cheat.util;

import java.util.concurrent.ThreadLocalRandom;

public final class MathUtil {

    private MathUtil() {
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double lerp(double from, double to, double delta) {
        return from + (to - from) * delta;
    }

    /** Uniform random value in {@code [min, max)}. */
    public static double random(double min, double max) {
        if (max <= min) {
            return min;
        }
        return min + ThreadLocalRandom.current().nextDouble() * (max - min);
    }

    /** Uniform random integer in {@code [min, max]}. */
    public static int random(int min, int max) {
        if (max <= min) {
            return min;
        }
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static boolean chance(double probability) {
        return ThreadLocalRandom.current().nextDouble() < probability;
    }
}
