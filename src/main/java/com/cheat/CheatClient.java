package com.cheat;

import com.cheat.event.EventBus;
import com.cheat.module.ModuleManager;
import com.cheat.state.StateSync;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Client entry point. Wires the event bus, module manager, state synchroniser and the
 * Fabric API render hook together.
 */
public final class CheatClient implements ClientModInitializer {

    public static final String MOD_ID = "cheat";
    public static final String MOD_NAME = "Cheat";

    private static CheatClient instance;

    private final EventBus eventBus = new EventBus();
    private final StateSync stateSync = new StateSync();

    @Override
    public void onInitializeClient() {
        instance = this;

        ModuleManager.init(eventBus);

        // Keybindings are owned by the modules, registration goes through Fabric API.
        ModuleManager.getModules().stream()
                .filter(module -> !module.isInfrastructure())
                .forEach(module ->
                        module.setKeyBinding(KeyBindingHelper.registerKeyBinding(
                                new KeyBinding("key." + MOD_ID + "." + module.getName().toLowerCase(),
                                        InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,
                                        "key.category." + MOD_ID))));

        WorldRenderEvents.AFTER_ENTITIES.register(context ->
                ModuleManager.getModules().stream()
                        .filter(module -> module instanceof com.cheat.visual.VisualModule)
                        .filter(com.cheat.module.Module::isEnabled)
                        .forEach(module -> ((com.cheat.visual.VisualModule) module).render(context)));
    }

    public static CheatClient getInstance() {
        return instance;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public StateSync getStateSync() {
        return stateSync;
    }

    /** Posts a client tick to the bus. Called from {@code MinecraftClientMixin}. */
    public void tick(net.minecraft.client.MinecraftClient client) {
        ModuleManager.tick(client);
    }
}
