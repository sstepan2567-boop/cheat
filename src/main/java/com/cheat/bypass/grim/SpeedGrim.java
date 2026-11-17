package com.cheat.bypass.grim;

import com.cheat.bypassAnticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.PacketEvent;
import com.cheat.event.TickEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.module.ModuleManager;
import com.cheat.movement.CollideReset;
import com.cheat.movement.PlayerMoveRewriter;
import com.cheat.packet.PacketLayer;
import com.cheat.util.PlayerUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/**
 * Grim speed.
 *
 * <p>Submode {@code VelocityReset}: at the top of every jump the horizontal velocity is
 * cancelled through a collision reset ({@link CollideReset#resetAtApex}). For one tick
 * after the reset the outgoing {@link PlayerMoveC2SPacket} is rewritten with
 * {@code onGround = true} so Grim's ground-speed check sees a collision instead of a
 * sprint-jump speed gain.
 */
public final class SpeedGrim extends BypassModule {

    public enum Submode {
        VELOCITY_RESET
    }

    private final CollideReset collideReset;
    private Submode submode = Submode.VELOCITY_RESET;
    private int resetTimer;

    public SpeedGrim() {
        super("SpeedGrim", "Grim speed: velocity reset through a block collision at the jump apex.",
                ModuleCategory.MOVEMENT, Anticheat.GRIM);
        this.collideReset = ModuleManager.getModule(CollideReset.class);
    }

    @Override
    public void onTick(TickEvent event) {
        if (resetTimer > 0) {
            resetTimer--;
        }
        ClientPlayerEntity player = event.getPlayer();
        if (player == null || collideReset == null) {
            return;
        }
        if (submode == Submode.VELOCITY_RESET
                && !player.isOnGround()
                && player.getVelocity().y <= 0.0d
                && PlayerUtil.getHorizontalSpeed(player) > 0.10d) {
            collideReset.resetAtApex(player);
            resetTimer = 1;
        }
    }

    @Override
    public void onPacket(PacketEvent event) {
        if (!event.isOutgoing() || !(event.getPacket() instanceof PlayerMoveC2SPacket move)) {
            return;
        }
        if (resetTimer > 0 && !move.isOnGround()) {
            event.cancel();
            PacketLayer.sendRaw(client(), PlayerMoveRewriter.withOnGround(move, true));
        }
    }

    public Submode getSubmode() {
        return submode;
    }

    public void setSubmode(Submode submode) {
        this.submode = submode;
    }
}
