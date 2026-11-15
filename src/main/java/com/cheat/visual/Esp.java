package com.cheat.visual;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/**
 * Player ESP. Draws a translucent filled box around every player except the local one.
 * Box coordinates are shifted by the camera position because the world matrix stack used
 * by {@code WorldRenderEvents} is in world space.
 */
public final class Esp extends VisualModule {

    private float red = 0.30f;
    private float green = 0.75f;
    private float blue = 1.00f;
    private float alpha = 0.85f;
    private double expand = 0.05d;

    public Esp() {
        super("ESP", "Draws a translucent box around players.");
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
        Vec3d camera = context.camera().getPos();
        for (PlayerEntity player : context.world().getPlayers()) {
            if (player == self) {
                continue;
            }
            Box box = player.getBoundingBox().expand(expand).offset(-camera.x, -camera.y, -camera.z);
            net.minecraft.client.render.debug.DebugRenderer.drawBox(matrices, consumers, box,
                    red, green, blue, alpha);
        }
    }

    public void setColor(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }
}
