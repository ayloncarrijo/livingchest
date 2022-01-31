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
  private final int xOffset;
  private final int yOffset;
  private final int yPlayer;
  private final ChesterEntity chester;

  public ChesterContainer(
      final EntityPlayer player,
      final ChesterEntity chester) {
    this.invCols = 9;
    this.invRows = chester.getInventory().getSlots() / this.invCols;
    this.xOffset = 8;
    this.yOffset = 18;
    this.yPlayer = (invRows - 4) * 18;
    this.chester = chester;
    this.chester.getInventory().onOpenInventory(player);

    IntStream.range(0, invRows).forEach((row) -> {
      IntStream.range(0, invCols).forEach((col) -> {
        final int index = row * 9 + col;
        final int xPos = col * 18 + xOffset;
        final int yPos = row * 18 + yOffset;

        this.addSlotToContainer(
            new SlotItemHandler(
                this.chester.getInventory(),
                index,
                xPos,
                yPos));
      });
    });

    IntStream.range(0, 3).forEach((row) -> {
      IntStream.range(0, 9).forEach((col) -> {
        final int index = row * 9 + col + 9;
        final int xPos = col * 18 + xOffset;
        final int yPos = row * 18 + 103 + yPlayer;

        this.addSlotToContainer(new Slot(
            player.inventory,
            index,
            xPos,
            yPos));
      });
    });

    IntStream.range(0, 9).forEach((col) -> {
      final int index = col;
      final int xPos = col * 18 + xOffset;
      final int yPos = 161 + yPlayer;

      this.addSlotToContainer(new Slot(
          player.inventory,
          index,
          xPos,
          yPos));
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
        if (!this.mergeItemStack(
            itemStackInSlot,
            this.chester.getInventory().getSlots(),
            this.inventorySlots.size(),
            true)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.mergeItemStack(
          itemStackInSlot,
          0,
          this.chester.getInventory().getSlots(),
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
