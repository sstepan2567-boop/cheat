package com.cheat.mixin;

import com.cheat.CheatClient;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Posts the per-tick event and drives the module keybindings. */
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void cheat$onTick(CallbackInfo ci) {
        CheatClient client = CheatClient.getInstance();
        if (client != null) {
            client.tick((MinecraftClient) (Object) this);
        }
    }
}
