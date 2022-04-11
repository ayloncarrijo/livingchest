package com.syllient.livingchest.util;

import java.util.stream.IntStream;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class InventoryUtil {
  public static void dropItems(final World world, final BlockPos pos,
      final IItemHandler inventory) {
    dropItems(world, pos.getX(), pos.getY(), pos.getZ(), inventory);
  }

  public static void dropItems(final World world, final Entity entity,
      final IItemHandler inventory) {
    dropItems(world, entity.getX(), entity.getY(), entity.getZ(), inventory);
  }

  public static void dropItems(final World world, final double x, final double y, final double z,
      final IItemHandler inventory) {
    IntStream.range(0, inventory.getSlots()).forEach((slot) -> {
      final ItemStack itemStack = inventory.getStackInSlot(slot);

      if (!itemStack.isEmpty()) {
        InventoryHelper.dropItemStack(world, x, y, z, itemStack);
      }
    });
  }
}
