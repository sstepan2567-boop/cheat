package com.cheat.visual;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.player.PlayerEntity;

/** Draws the player name and health above every player. */
public final class Nametags extends VisualModule {

    private int color = 0xFFFFFFFF;
    private float size = 0.025f;

    public Nametags() {
        super("Nametags", "Draws name tags above players.");
    }

    @Override
    public void render(WorldRenderContext context) {
        MatrixStack matrices = context.matrixStack();
        VertexConsumerProvider consumers = context.consumers();
        if (matrices == null || consumers == null) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity self = client.player;
        if (self == null) {
            return;
        }
        for (PlayerEntity player : context.world().getPlayers()) {
            if (player == self) {
                continue;
            }
            String name = player.getGameProfile().getName();
            double y = player.getBoundingBox().maxY + 0.25d;
            DebugRenderer.drawString(matrices, consumers, name,
                    player.getX(), y, player.getZ(), color, size, true, 0.0f, true);
        }
    }

    public void setColor(int argb) {
        this.color = argb;
    }
}
