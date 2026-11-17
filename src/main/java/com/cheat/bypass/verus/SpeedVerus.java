package com.cheat.bypass.verus;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.PacketEvent;
import com.cheat.event.TickEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.module.ModuleManager;
import com.cheat.movement.MicroJump;
import com.cheat.movement.PlayerMoveRewriter;
import com.cheat.packet.PacketLayer;
import com.cheat.util.PlayerUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

/**
 * Verus speed - {@code VerusB3882}.
 *
 * <p>A micro bunny hop with a controlled Y step (0.22). The outgoing movement packet is
 * rewritten to {@code onGround = true} while a teleport transaction is outstanding, so the
 * speed gain is bound to the transaction window Verus expects and the Y delta stays inside
 * the check.
 */
public final class SpeedVerus extends BypassModule {

    private static final double JUMP_HEIGHT = 0.22d;

    private final MicroJump microJump;

    public SpeedVerus() {
        super("SpeedVerus", "Verus speed (B3882): transaction bound micro bunny hop.",
                ModuleCategory.MOVEMENT, Anticheat.VERUS);
        this.microJump = ModuleManager.getModule(MicroJump.class);
    }

    @Override
    public void onTick(TickEvent event) {
        ClientPlayerEntity player = event.getPlayer();
        if (player == null || microJump == null) {
            return;
        }
        if (player.isOnGround() && PlayerUtil.getHorizontalSpeed(player) > 0.10d) {
            microJump.microJump(player, JUMP_HEIGHT);
        }
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
