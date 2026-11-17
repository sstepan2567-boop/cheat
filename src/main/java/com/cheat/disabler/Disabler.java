package com.cheat.disabler;

import com.cheat.bypass.Anticheat;
import com.cheat.event.PacketEvent;
import com.cheat.module.Module;
import com.cheat.module.ModuleCategory;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * Disabler module.
 *
 * <p>Wraps one {@link DisablerProfile} per anticheat. The active profile is selected with
 * {@link #setAnticheat(Anticheat)}; outgoing packets go through
 * {@link DisablerProfile#applyC2S(PacketEvent)} and incoming packets through
 * {@link DisablerProfile#onS2C(PacketEvent)}.
 */
public final class Disabler extends Module {

    private final Map<Anticheat, DisablerProfile> profiles = new EnumMap<>(Anticheat.class);
    private DisablerProfile active;

    public Disabler() {
        super("Disabler", "Per-anticheat disabler profiles: transaction delay, ping substitution, timer compensation.",
                ModuleCategory.DISABLER);
        profiles.put(Anticheat.VERUS, new DisablerVerus());
        profiles.put(Anticheat.INTAVE, new DisablerIntave());
        profiles.put(Anticheat.GRIM, new DisablerGrim());
        profiles.put(Anticheat.MATRIX, new DisablerMatrix());
        profiles.put(Anticheat.VULCAN, new DisablerVulcan());
        active = profiles.get(Anticheat.VERUS);
    }

    @Override
    public void onEnable() {
        if (active != null) {
            active.onEnable();
        }
    }

    @Override
    public void onDisable() {
        if (active != null) {
            active.onDisable();
        }
    }

    @Override
    public void onPacket(PacketEvent event) {
        if (active == null) {
            return;
        }
        if (event.isOutgoing()) {
            if (active.applyC2S(event)) {
                event.cancel();
            }
        } else {
            active.onS2C(event);
        }
    }

    /** Switches the active profile. Enabling/disabling the module keeps the profile state. */
    public void setAnticheat(Anticheat anticheat) {
        if (active != null && active.getAnticheat() == anticheat) {
            return;
        }
        if (active != null) {
            active.onDisable();
        }
        active = profiles.get(anticheat);
        if (active != null && isEnabled()) {
            active.onEnable();
        }
    }

    public Anticheat getAnticheat() {
        return active == null ? null : active.getAnticheat();
    }

    public Collection<Anticheat> getSupportedAnticheats() {
        return profiles.keySet();
    }
}
