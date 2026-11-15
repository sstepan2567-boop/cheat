package com.cheat.bypass;

import com.cheat.CheatClient;
import com.cheat.mixin.ClientCommonNetworkHandlerMixin;
import com.cheat.module.Module;
import com.cheat.module.ModuleCategory;
import com.cheat.module.ModuleManager;
import com.cheat.movement.PacketQueueModule;
import com.cheat.movement.PingPongModule;
import com.cheat.state.StateSync;
import net.minecraft.client.MinecraftClient;

/**
 * Base class for every anticheat specific mode.
 *
 * <p>Modes are toggled by the user; the active anticheat is selected in the Disabler
 * settings. Every mode documents which packets it rewrites and which check it targets.
 */
public abstract class BypassModule extends Module {

    private final Anticheat anticheat;

    protected BypassModule(String name, String description, ModuleCategory category, Anticheat anticheat) {
        super(name, description, category);
        this.anticheat = anticheat;
    }

    public Anticheat getAnticheat() {
        return anticheat;
    }

    protected MinecraftClient client() {
        return MinecraftClient.getInstance();
    }

    protected StateSync state() {
        return CheatClient.getInstance().getStateSync();
    }

    protected PacketQueueModule queue() {
        return ModuleManager.getModule(PacketQueueModule.class);
    }

    protected PingPongModule ping() {
        return ModuleManager.getModule(PingPongModule.class);
    }
}
