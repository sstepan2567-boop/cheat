package com.cheat.bypass.vulcan;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.PacketEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.packet.PacketLayer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;

/**
 * Vulcan scaffold.
 *
 * <p>Reorders the packet burst around a block place: {@code START_SPRINTING} and
 * {@code RELEASE_SHIFT_KEY} are written before the {@link PlayerInteractBlockC2SPacket}.
 * Vulcan's scaffold check expects the sprint state change and the un-sneak to arrive ahead
 * of the place, so the rewritten order keeps the place from being flagged.
 */
public final class ScaffoldVulcan extends BypassModule {

    public ScaffoldVulcan() {
        super("ScaffoldVulcan", "Vulcan scaffold: sprint / un-sneak packets are sent before the block place.",
                ModuleCategory.MOVEMENT, Anticheat.VULCAN);
    }

    @Override
    public void onPacket(PacketEvent event) {
        if (!event.isOutgoing() || !(event.getPacket() instanceof PlayerInteractBlockC2SPacket packet)) {
            return;
        }
        ClientPlayerEntity player = client().player;
        if (player == null) {
            return;
        }
        event.cancel();
        PacketLayer.sendRaw(client(), new ClientCommandC2SPacket(player,
                ClientCommandC2SPacket.Mode.START_SPRINTING));
        PacketLayer.sendRaw(client(), new ClientCommandC2SPacket(player,
                ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        PacketLayer.sendRaw(client(), packet);
    }
}
