package com.cheat.mixin;

import com.cheat.event.PacketEvent;
import com.cheat.module.ModuleManager;
import com.cheat.packet.PacketLayer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.TickStepS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateTickRateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fires {@link PacketEvent} for the incoming packets the bypass layer cares about and stops
 * vanilla from processing cancelled ones.
 */
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onEntityVelocityUpdate", at = @At("HEAD"), cancellable = true)
    private void cheat$onEntityVelocityUpdate(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
        post(packet, ci);
    }

    @Inject(method = "onPlayerPositionLook", at = @At("HEAD"), cancellable = true)
    private void cheat$onPlayerPositionLook(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        post(packet, ci);
    }

    @Inject(method = "onPlayerActionResponse", at = @At("HEAD"), cancellable = true)
    private void cheat$onPlayerActionResponse(PlayerActionResponseS2CPacket packet, CallbackInfo ci) {
        post(packet, ci);
    }

    @Inject(method = "onEntity", at = @At("HEAD"), cancellable = true)
    private void cheat$onEntity(EntityS2CPacket packet, CallbackInfo ci) {
        post(packet, ci);
    }

    @Inject(method = "onEntityStatus", at = @At("HEAD"), cancellable = true)
    private void cheat$onEntityStatus(EntityStatusS2CPacket packet, CallbackInfo ci) {
        post(packet, ci);
    }

    @Inject(method = "onHealthUpdate", at = @At("HEAD"), cancellable = true)
    private void cheat$onHealthUpdate(HealthUpdateS2CPacket packet, CallbackInfo ci) {
        post(packet, ci);
    }

    @Inject(method = "onGameStateChange", at = @At("HEAD"), cancellable = true)
    private void cheat$onGameStateChange(GameStateChangeS2CPacket packet, CallbackInfo ci) {
        post(packet, ci);
    }

    @Inject(method = "onExplosion", at = @At("HEAD"), cancellable = true)
    private void cheat$onExplosion(ExplosionS2CPacket packet, CallbackInfo ci) {
        post(packet, ci);
    }

    @Inject(method = "onUpdateTickRate", at = @At("HEAD"), cancellable = true)
    private void cheat$onUpdateTickRate(UpdateTickRateS2CPacket packet, CallbackInfo ci) {
        post(packet, ci);
    }

    @Inject(method = "onTickStep", at = @At("HEAD"), cancellable = true)
    private void cheat$onTickStep(TickStepS2CPacket packet, CallbackInfo ci) {
        post(packet, ci);
    }

    private void post(Packet<?> packet, CallbackInfo ci) {
        if (PacketLayer.isSuppressed()) {
            return;
        }
        PacketEvent event = new PacketEvent(packet, PacketEvent.Direction.INCOMING);
        ModuleManager.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
