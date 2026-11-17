package com.cheat.bypass.intave;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.PacketEvent;
import com.cheat.event.TickEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.module.ModuleManager;
import com.cheat.movement.MicroJump;
import com.cheat.util.MathUtil;
import com.cheat.util.PlayerUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

/**
 * Intave speed - {@code 1.4}.
 *
 * <p>Controls the delay of the movement packets and binds them to the teleport transaction:
 * while a transaction is outstanding the packet is held back for a randomised delay, and a
 * micro jump is applied on the ground. Intave samples the movement rhythm, so the jitter
 * keeps the speed gain from looking like a constant timer.
 */
public final class SpeedIntave14 extends BypassModule {

    private static final double JUMP_HEIGHT = 0.24d;

    private final MicroJump microJump;

    public SpeedIntave14() {
        super("SpeedIntave14", "Intave speed (1.4): packet delay bound to the transaction.",
                ModuleCategory.MOVEMENT, Anticheat.INTAVE);
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
                queue().sendDelayed(move, (long) MathUtil.random(15L, 35L));
            }
        }
    }
}
