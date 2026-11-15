package com.cheat.visual;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

/** Draws a line from the camera to every player. */
public final class Tracers extends VisualModule {

    private int color = 0xFF33CCFF;

    public Tracers() {
        super("Tracers", "Draws a line from the camera to each player.");
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
        VertexConsumer buffer = consumers.getBuffer(RenderLayer.getLines());
        Vec3d camera = context.camera().getPos();
        Vector3f origin = new Vector3f(0.0f, 0.0f, 0.0f);
        for (PlayerEntity player : context.world().getPlayers()) {
            if (player == self) {
                continue;
            }
            Vec3d end = player.getBoundingBox().getCenter().subtract(camera);
            VertexRendering.drawVector(matrices, buffer, origin, end, color);
        }
    }

    public void setColor(int argb) {
        this.color = argb;
    }
}
