package com.syllient.livingchest.container;

import java.util.stream.IntStream;
import com.syllient.livingchest.entity.ChesterEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ChesterContainer extends Container {
  private final int invCols;
  private final int invRows;
  private final int offsetX;
  private final int offsetY;
  private final ChesterEntity chester;

  public ChesterContainer(final EntityPlayer player, final ChesterEntity chester) {
    this.invCols = 9;
    this.invRows = chester.getInventory().getSlots() / this.invCols;
    this.offsetX = 8;
    this.offsetY = 18;
    this.chester = chester;
    this.chester.getInventory().onOpenInventory(player);

    IntStream.range(0, invRows).forEach((row) -> {
      IntStream.range(0, invCols).forEach((col) -> {
        final int index = row * 9 + col;
        final int posX = col * 18 + offsetX;
        final int posY = row * 18 + offsetY;

        this.addSlotToContainer(
            new SlotItemHandler(this.chester.getInventory(), index, posX, posY));
      });
    });

    IntStream.range(0, 3).forEach((row) -> {
      IntStream.range(0, 9).forEach((col) -> {
        final int index = row * 9 + col + 9;
        final int posX = col * 18 + offsetX;
        final int posY = row * 18 + 103 + (invRows - 4) * 18;

        this.addSlotToContainer(new Slot(player.inventory, index, posX, posY));
      });
    });

    IntStream.range(0, 9).forEach((col) -> {
      final int index = col;
      final int posX = col * 18 + offsetX;
      final int posY = 161 + (invRows - 4) * 18;

      this.addSlotToContainer(new Slot(player.inventory, index, posX, posY));
    });
  }

  @Override
  public boolean canInteractWith(final EntityPlayer playerIn) {
    return true;
  }

  @Override
  public void onContainerClosed(final EntityPlayer playerIn) {
    super.onContainerClosed(playerIn);
    this.chester.getInventory().onCloseInventory(playerIn);
  }

  @Override
  public ItemStack transferStackInSlot(final EntityPlayer playerIn, final int index) {
    ItemStack itemStack = ItemStack.EMPTY;
    final Slot invSlot = this.inventorySlots.get(index);

    if (invSlot != null && invSlot.getHasStack()) {
      final ItemStack itemStackInSlot = invSlot.getStack();
      itemStack = itemStackInSlot.copy();

      if (index < this.chester.getInventory().getSlots()) {
        if (!this.mergeItemStack(itemStackInSlot, this.chester.getInventory().getSlots(),
            this.inventorySlots.size(), true)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.mergeItemStack(itemStackInSlot, 0, this.chester.getInventory().getSlots(),
          false)) {
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
