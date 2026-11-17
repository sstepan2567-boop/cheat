package com.cheat.module;

import com.cheat.event.EventHandler;
import com.cheat.event.PacketEvent;
import com.cheat.event.TickEvent;
import net.minecraft.client.option.KeyBinding;

/**
 * Base class for every feature module.
 *
 * <p>Modules are registered once in {@link ModuleManager#init}. Subclasses override
 * {@link #onTick(TickEvent)} and/or {@link #onPacket(PacketEvent)}; both are annotated
 * on the base class so the event bus discovers them through the class hierarchy.
 */
public abstract class Module {

    private final String name;
    private final String description;
    private final ModuleCategory category;
    private final boolean infrastructure;
    private boolean enabled;
    private KeyBinding keyBinding;

    protected Module(String name, String description, ModuleCategory category) {
        this(name, description, category, false);
    }

    /**
     * @param infrastructure when {@code true} the module is always ticked, even while it is
     *                       toggled off. Used for the packet queue and the ping probe that
     *                       other modes depend on.
     */
    protected Module(String name, String description, ModuleCategory category, boolean infrastructure) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.infrastructure = infrastructure;
    }

    public void toggle() {
        enabled = !enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    @EventHandler
    public void onTick(TickEvent event) {
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ModuleCategory getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isInfrastructure() {
        return infrastructure;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            toggle();
        }
    }

    public KeyBinding getKeyBinding() {
        return keyBinding;
    }

    public void setKeyBinding(KeyBinding keyBinding) {
        this.keyBinding = keyBinding;
    }

    @Override
    public String toString() {
        return name;
    }
}
