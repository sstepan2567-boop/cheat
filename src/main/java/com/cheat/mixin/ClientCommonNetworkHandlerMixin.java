package com.cheat.mixin;

import com.cheat.event.PacketEvent;
import com.cheat.module.ModuleManager;
import com.cheat.packet.PacketLayer;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Fires {@link PacketEvent} for every outgoing packet and honours cancellations. */
@Mixin(ClientCommonNetworkHandler.class)
public class ClientCommonNetworkHandlerMixin {

    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    private void cheat$onSendPacket(Packet<?> packet, CallbackInfo ci) {
        if (PacketLayer.isSuppressed()) {
            return;
        }
        PacketEvent event = new PacketEvent(packet, PacketEvent.Direction.OUTGOING);
        ModuleManager.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
