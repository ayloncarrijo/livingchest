package com.syllient.livingchest.container;

import java.util.stream.IntStream;
import com.syllient.livingchest.entity.ChesterEntity;
import com.syllient.livingchest.inventory.ChesterInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ChesterContainer extends Container {
  public final ChesterInventory inventory;
  public final ChesterEntity chester;
  public final int cols;
  public final int rows;
  public final int offsetX;
  public final int offsetY;

  public ChesterContainer(final EntityPlayer player, final ChesterEntity chester) {
    this.inventory = chester.getInventory();
    this.chester = chester;
    this.cols = 9;
    this.rows = this.inventory.getSlots() / this.cols;
    this.offsetX = 8;
    this.offsetY = 18;
    this.inventory.handleInventoryOpening(player);

    IntStream.range(0, this.rows).forEach((row) -> {
      IntStream.range(0, this.cols).forEach((col) -> {
        final int index = row * 9 + col;
        final int posX = col * 18 + this.offsetX;
        final int posY = row * 18 + this.offsetY;

        this.addSlotToContainer(new SlotItemHandler(this.inventory, index, posX, posY));
      });
    });

    IntStream.range(0, 3).forEach((row) -> {
      IntStream.range(0, 9).forEach((col) -> {
        final int index = row * 9 + col + 9;
        final int posX = col * 18 + this.offsetX;
        final int posY = row * 18 + 103 + (this.rows - 4) * 18;

        this.addSlotToContainer(new Slot(player.inventory, index, posX, posY));
      });
    });

    IntStream.range(0, 9).forEach((col) -> {
      final int index = col;
      final int posX = col * 18 + this.offsetX;
      final int posY = 161 + (this.rows - 4) * 18;

      this.addSlotToContainer(new Slot(player.inventory, index, posX, posY));
    });
  }

  @Override
  public boolean canInteractWith(final EntityPlayer player) {
    return this.chester.isEntityAlive() && this.chester.getDistance(player) <= 8;
  }

  @Override
  public void onContainerClosed(final EntityPlayer player) {
    super.onContainerClosed(player);
    this.inventory.handleInventoryClosure(player);
  }

  @Override
  public ItemStack transferStackInSlot(final EntityPlayer player, final int index) {
    ItemStack itemStack = ItemStack.EMPTY;
    final Slot invSlot = this.inventorySlots.get(index);

    if (invSlot != null && invSlot.getHasStack()) {
      final ItemStack itemStackInSlot = invSlot.getStack();
      itemStack = itemStackInSlot.copy();

      if (index < this.inventory.getSlots()) {
        if (!this.mergeItemStack(itemStackInSlot, this.inventory.getSlots(),
            this.inventorySlots.size(), true)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.mergeItemStack(itemStackInSlot, 0, this.inventory.getSlots(), false)) {
        return ItemStack.EMPTY;
      }

      if (itemStackInSlot.isEmpty()) {
        invSlot.putStack(ItemStack.EMPTY);
      } else {
        invSlot.onSlotChanged();
      }
    }

    return itemStack;
  }
}
