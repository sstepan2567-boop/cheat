package com.cheat.bypass;

/** Anticheats the bypass layer ships profiles for. */
public enum Anticheat {
    GRIM("Grim"),
    VULCAN("Vulcan"),
    VERUS("Verus"),
    MATRIX("Matrix"),
    NCP("NCP"),
    SPARTAN("Spartan"),
    INTAVE("Intave"),
    HYPIXEL("Watchdog (Hypixel)");

    private final String displayName;

    Anticheat(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
