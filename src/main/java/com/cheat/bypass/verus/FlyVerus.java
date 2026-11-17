package com.cheat.bypass.verus;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.PacketEvent;
import com.cheat.event.TickEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.movement.PlayerMoveRewriter;
import com.cheat.packet.PacketLayer;
import com.cheat.util.MathUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

/**
 * Verus fly.
 *
 * <p>Low flight with a randomised vertical step in {@code [-0.05, 0.05]}. While a teleport
 * transaction is outstanding the outgoing movement packet is rewritten to
 * {@code onGround = true}, which keeps Verus's fall/flight check from seeing a clean
 * vertical teleport.
 */
public final class FlyVerus extends BypassModule {

    public FlyVerus() {
        super("FlyVerus", "Verus fly: low flight with fall control bound to the transaction.",
                ModuleCategory.MOVEMENT, Anticheat.VERUS);
    }

    @Override
    public void onTick(TickEvent event) {
        ClientPlayerEntity player = event.getPlayer();
        if (player == null) {
            return;
        }
        double step = MathUtil.random(-0.05d, 0.05d);
        player.setVelocity(player.getVelocity().x, step, player.getVelocity().z);
        player.setOnGround(false);
    }

    @Override
    public void onPacket(PacketEvent event) {
        if (event.isIncoming() && event.getPacket() instanceof PlayerPositionLookS2CPacket teleport) {
            state().onTeleport(teleport.teleportId());
            return;
        }
        if (event.isOutgoing() && event.getPacket() instanceof PlayerMoveC2SPacket move) {
            if (state().hasPendingTeleports() && !move.isOnGround()) {
                event.cancel();
                PacketLayer.sendRaw(client(), PlayerMoveRewriter.withOnGround(move, true));
            }
        }
    }
}
