package com.cheat.module;

import com.cheat.event.EventBus;
import com.cheat.event.TickEvent;
import com.cheat.event.PacketEvent;
import com.cheat.module.Module;
import com.cheat.CheatClient;
import com.cheat.visual.Esp;
import com.cheat.visual.Nametags;
import com.cheat.visual.Tracers;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds every module instance, registers them on the event bus and dispatches ticks
 * and packet events.
 */
public final class ModuleManager {

    private static final List<Module> MODULES = new ArrayList<>();

    private ModuleManager() {
    }

    public static void init(EventBus eventBus) {
        // Core movement helpers.
        register(new com.cheat.movement.CollideReset());
        register(new com.cheat.movement.MicroJump());
        register(new com.cheat.movement.PacketQueueModule());
        register(new com.cheat.movement.PingPongModule());

        // Grim.
        register(new com.cheat.bypass.grim.SpeedGrim());
        register(new com.cheat.bypass.grim.FlyGrim());
        register(new com.cheat.bypass.grim.VelocityGrim());
        register(new com.cheat.bypass.grim.ReachGrim());
        register(new com.cheat.bypass.grim.NoFallGrim());

        // Vulcan.
        register(new com.cheat.bypass.vulcan.SpeedVulcan());
        register(new com.cheat.bypass.vulcan.FlyVulcan());
        register(new com.cheat.bypass.vulcan.ScaffoldVulcan());
        register(new com.cheat.bypass.vulcan.CombatVulcan());
        register(new com.cheat.bypass.vulcan.NoFallVulcan());

        // Verus.
        register(new com.cheat.bypass.verus.CombatVerus());
        register(new com.cheat.bypass.verus.SpeedVerus());
        register(new com.cheat.bypass.verus.ScaffoldVerus());
        register(new com.cheat.bypass.verus.FlyVerus());

        // Matrix.
        register(new com.cheat.bypass.matrix.SpeedMatrix());
        register(new com.cheat.bypass.matrix.FlyMatrix());
        register(new com.cheat.bypass.matrix.LongJumpMatrix());
        register(new com.cheat.bypass.matrix.NoFallMatrix());

        // NCP.
        register(new com.cheat.bypass.ncp.SpeedNCP());
        register(new com.cheat.bypass.ncp.FlyNcpClip());
        register(new com.cheat.bypass.ncp.NoFallNCP());

        // Spartan.
        register(new com.cheat.bypass.spartan.SpeedSpartan());
        register(new com.cheat.bypass.spartan.FlySpartan524());
        register(new com.cheat.bypass.spartan.NoFallSpartan());

        // Intave.
        register(new com.cheat.bypass.intave.SpeedIntave14());
        register(new com.cheat.bypass.intave.CombatIntave());

        // Hypixel / Watchdog.
        register(new com.cheat.bypass.hypixel.SpeedHypixelBHop());
        register(new com.cheat.bypass.hypixel.SpeedHypixelLowHop());
        register(new com.cheat.bypass.hypixel.FlyHypixelFlat());
        register(new com.cheat.bypass.hypixel.NoFallHypixel());

        // Disabler layer.
        register(new com.cheat.disabler.Disabler());

        // Visualisation.
        register(new Esp());
        register(new Tracers());
        register(new Nametags());

        MODULES.forEach(eventBus::register);
    }

    private static void register(Module module) {
        MODULES.add(module);
    }

    public static List<Module> getModules() {
        return MODULES;
    }

    public static <T extends Module> T getModule(Class<T> type) {
        for (Module module : MODULES) {
            if (type.isInstance(module)) {
                return type.cast(module);
            }
        }
        return null;
    }

    /** Handles key presses and posts the per-tick event. */
    public static void tick(MinecraftClient client) {
        for (Module module : MODULES) {
            if (!module.isInfrastructure() && module.getKeyBinding() != null
                    && module.getKeyBinding().wasPressed()) {
                module.toggle();
            }
        }
        CheatClient.getInstance().getEventBus().post(new TickEvent(client));
    }

    /** Posts a packet event without going through the mixins (used by helpers). */
    public static void post(PacketEvent event) {
        CheatClient.getInstance().getEventBus().post(event);
    }
}
