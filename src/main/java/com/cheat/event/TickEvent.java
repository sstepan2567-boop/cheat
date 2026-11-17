package com.cheat.event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

/** Posted once per client tick from {@code MinecraftClientMixin}. */
public final class TickEvent extends Event {

    private final MinecraftClient client;

    public TickEvent(MinecraftClient client) {
        this.client = client;
    }

    public MinecraftClient getClient() {
        return client;
    }

    public ClientPlayerEntity getPlayer() {
        return client.player;
    }
}
