package com.cheat.bypass.grim;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.PacketEvent;
import com.cheat.event.TickEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.util.MathUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

/**
 * Grim fly - transaction delayed vertical (spectate style).
 *
 * <p>The player is held in the air with a small vertical step every tick. The
 * {@link TeleportConfirmC2SPacket} that answers a {@link PlayerPositionLookS2CPacket} is
 * queued for a few milliseconds, so the vertical movement is applied before the server
 * teleports the player back. That keeps Grim's flight check from seeing a clean vertical
 * teleport while still allowing the climb.
 */
public final class FlyGrim extends BypassModule {

    private double climb = 0.12d;
    private long confirmDelayMillis = 40L;

    public FlyGrim() {
        super("FlyGrim", "Grim fly: transaction delayed vertical climb.",
                ModuleCategory.MOVEMENT, Anticheat.GRIM);
    }

    @Override
    public void onTick(TickEvent event) {
        ClientPlayerEntity player = event.getPlayer();
        if (player == null) {
            return;
        }
        player.setVelocity(player.getVelocity().x, climb, player.getVelocity().z);
        player.setOnGround(false);
    }

    @Override
    public void onPacket(PacketEvent event) {
        if (event.isIncoming() && event.getPacket() instanceof PlayerPositionLookS2CPacket teleport) {
            state().onTeleport(teleport.teleportId());
            return;
        }
        if (event.isOutgoing() && event.getPacket() instanceof TeleportConfirmC2SPacket confirm) {
            event.cancel();
            queue().sendDelayed(confirm, (long) MathUtil.random(confirmDelayMillis, confirmDelayMillis + 20L));
        }
    }

    public double getClimb() {
        return climb;
    }

    public void setClimb(double climb) {
        this.climb = climb;
    }
}
