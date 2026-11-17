package com.cheat.bypass.vulcan;

import com.cheat.bypass.Anticheat;
import com.cheat.bypass.BypassModule;
import com.cheat.event.TickEvent;
import com.cheat.module.ModuleCategory;
import com.cheat.util.MathUtil;
import com.cheat.util.PlayerUtil;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Vulcan fly.
 *
 * <p>Submode {@code Vulcan277}: riptide abuse - when a trident is present in the hotbar the
 * mode selects it and launches the player with a strong vertical impulse, then keeps the
 * player airborne with a small holding velocity. Submode {@code Vulcan286_18}: low flight
 * with a randomised vertical step in {@code [-0.08, 0.08]}, which stays under Vulcan's
 * flight flag threshold.
 */
public final class FlyVulcan extends BypassModule {

    public enum Submode {
        VULCAN277,
        VULCAN286_18
    }

    private Submode submode = Submode.VULCAN277;
    private int launchTicks;

    public FlyVulcan() {
        super("FlyVulcan", "Vulcan fly: riptide abuse (277) or low randomised flight (286.18).",
                ModuleCategory.MOVEMENT, Anticheat.VULCAN);
    }

    @Override
    public void onTick(TickEvent event) {
        ClientPlayerEntity player = event.getPlayer();
        if (player == null) {
            return;
        }
        switch (submode) {
            case VULCAN277 -> {
                int tridentSlot = PlayerUtil.findTridentSlot(player);
                if (tridentSlot == -1) {
                    return;
                }
                if (player.getInventory().selectedSlot != tridentSlot) {
                    player.getInventory().selectedSlot = tridentSlot;
                }
                if (launchTicks > 0) {
                    launchTicks--;
                    player.setVelocity(player.getVelocity().x, 0.35d, player.getVelocity().z);
                } else {
                    player.setVelocity(player.getVelocity().x, 1.05d, player.getVelocity().z);
                    launchTicks = 3;
                }
                player.setOnGround(false);
            }
            case VULCAN286_18 -> {
                double step = MathUtil.random(-0.08d, 0.08d);
                player.setVelocity(player.getVelocity().x, step, player.getVelocity().z);
                player.setOnGround(false);
            }
        }
    }

    public Submode getSubmode() {
        return submode;
    }

    public void setSubmode(Submode submode) {
        this.submode = submode;
    }
}
