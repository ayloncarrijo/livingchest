package com.syllient.livingchest.inventory;

import java.util.stream.IntStream;

import com.syllient.livingchest.entity.EntityChester;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerChester extends Container {
  private final EntityChester chester;

  private final int invTotalSlots;
  private final int invCols;
  private final int invRows;
  private final int xOffset;
  private final int yOffset;
  private final int yOffsetPlayer;

  public ContainerChester(
      final EntityPlayer player,
      final EntityChester chester) {
    this.chester = chester;
    this.chester.getInventory().onOpenInventory(player);

    this.invTotalSlots = this.chester.getInventory().getSlots();
    this.invCols = 9;
    this.invRows = this.invTotalSlots / this.invCols;
    this.xOffset = 8;
    this.yOffset = 18;
    this.yOffsetPlayer = (invRows - 4) * 18;

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

    IntStream.range(0, 9).forEach((col) -> {
      final int index = col;
      final int xPos = col * 18 + xOffset;
      final int yPos = 161 + yOffsetPlayer;

      this.addSlotToContainer(new Slot(
          player.inventory,
          index,
          xPos,
          yPos));
    });

    IntStream.range(0, 3).forEach((row) -> {
      IntStream.range(0, 9).forEach((col) -> {
        final int index = row * 9 + col + 9;
        final int xPos = col * 18 + xOffset;
        final int yPos = row * 18 + 103 + yOffsetPlayer;

        this.addSlotToContainer(new Slot(
            player.inventory,
            index,
            xPos,
            yPos));
      });
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
    final Slot invSlot = this.inventorySlots.get(index);
    ItemStack itemStack = ItemStack.EMPTY;

    if (invSlot != null && invSlot.getHasStack()) {
      final ItemStack itemStackInSlot = invSlot.getStack();
      itemStack = itemStackInSlot.copy();

      if (index < this.invTotalSlots) {
        if (!this.mergeItemStack(
            itemStackInSlot,
            this.invTotalSlots,
            this.inventorySlots.size(),
            true)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.mergeItemStack(
          itemStackInSlot,
          0,
          this.invTotalSlots,
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
