package com.cheat.visual;

import com.cheat.module.Module;
import com.cheat.module.ModuleCategory;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

/** Base class for the overlay features driven by {@code WorldRenderEvents.AFTER_ENTITIES}. */
public abstract class VisualModule extends Module {

    protected VisualModule(String name, String description) {
        super(name, description, ModuleCategory.RENDER);
    }

    public abstract void render(WorldRenderContext context);
}
