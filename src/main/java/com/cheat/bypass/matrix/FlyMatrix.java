package com.cheat.bypass.matrix;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.TickEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.packet.PacketLayer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

/**
 * Matrix fly.
 *
 * <p>Every few ticks the mode toggles the sneak state in the air with
 * {@code PRESS_SHIFT_KEY} / {@code RELEASE_SHIFT_KEY}. Matrix keeps a flight flag that is
 * reset by a sneak transition; the periodic crouch clears it while a small upward velocity
 * keeps the player airborne.
 */
public final class FlyMatrix extends BypassModule {

    private static final int CROUCH_PERIOD = 4;

    private int timer;
    private boolean sneaking;

    public FlyMatrix() {
        super("FlyMatrix", "Matrix fly: periodic air crouch resets the flight flag.",
                ModuleCategory.MOVEMENT, Anticheat.MATRIX);
    }

    @Override
    public void onTick(TickEvent event) {
        ClientPlayerEntity player = event.getPlayer();
        if (player == null) {
            return;
        }
        player.setVelocity(player.getVelocity().x, 0.06d, player.getVelocity().z);
        player.setOnGround(false);

        if (timer > 0) {
            timer--;
            return;
        }
        if (sneaking) {
            PacketLayer.sendRaw(client(), new ClientCommandC2SPacket(player,
                    ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
            sneaking = false;
        } else {
            PacketLayer.sendRaw(client(), new ClientCommandC2SPacket(player,
                    ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
            sneaking = true;
        }
        timer = CROUCH_PERIOD;
    }

    @Override
    public void onDisable() {
        ClientPlayerEntity player = client().player;
        if (player != null && sneaking) {
            PacketLayer.sendRaw(client(), new ClientCommandC2SPacket(player,
                    ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        }
        sneaking = false;
        timer = 0;
    }
}
