package com.cheat.util;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/** Player / world inspection helpers used by the movement modes. */
public final class PlayerUtil {

    private PlayerUtil() {
    }

    public static boolean isAirborne(ClientPlayerEntity player) {
        return !player.isOnGround();
    }

    public static double getHorizontalSpeed(ClientPlayerEntity player) {
        return player.getVelocity().horizontalLength();
    }

    public static double getSpeed(ClientPlayerEntity player) {
        return player.getVelocity().length();
    }

    public static void resetHorizontalVelocity(ClientPlayerEntity player) {
        Vec3d velocity = player.getVelocity();
        player.setVelocity(0.0, velocity.y, 0.0);
    }

    public static void resetVelocity(ClientPlayerEntity player) {
        player.setVelocity(0.0, 0.0, 0.0);
    }

    public static void setVelocity(ClientPlayerEntity player, double x, double y, double z) {
        player.setVelocity(x, y, z);
    }

    public static double distanceTo(ClientPlayerEntity player, Entity entity) {
        return player.getPos().distanceTo(entity.getPos());
    }

    public static double distanceTo(ClientPlayerEntity player, Vec3d position) {
        return player.getPos().distanceTo(position);
    }

    /** True while the player is falling and has accumulated fall distance. */
    public static boolean isFalling(ClientPlayerEntity player) {
        return player.getVelocity().y < 0.0 && player.fallDistance > 0.0f;
    }

    public static boolean isInsideSolid(ClientPlayerEntity player) {
        BlockPos pos = player.getBlockPos();
        return !player.getWorld().getBlockState(pos).isAir();
    }

    /** Returns the hotbar slot containing a trident, or {@code -1}. */
    public static int findTridentSlot(ClientPlayerEntity player) {
        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = player.getInventory().getStack(slot);
            if (!stack.isEmpty() && stack.isOf(Items.TRIDENT)) {
                return slot;
            }
        }
        return -1;
    }

    public static boolean hasTrident(ClientPlayerEntity player) {
        return findTridentSlot(player) != -1;
    }

    /** Block position directly under the player's feet. */
    public static BlockPos getBlockBelow(ClientPlayerEntity player) {
        return BlockPos.ofFloored(player.getX(), player.getY() - 0.5d, player.getZ());
    }
}
