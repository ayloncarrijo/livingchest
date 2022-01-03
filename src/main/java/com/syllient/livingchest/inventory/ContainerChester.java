package com.syllient.livingchest.inventory;

import java.util.stream.IntStream;

import com.syllient.livingchest.entity.EntityChester;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerChester extends Container {
  private final EntityChester chester;

  public ContainerChester(
      final EntityPlayer player,
      final EntityChester chester) {
    this.chester = chester;
    this.chester.getInventory().onOpenInventory(player);

    final int inventoryCols = 9;
    final int inventoryRows = this.chester.getInventory().getSlots() / inventoryCols;
    final int xOffset = 8;
    final int yOffset = 18;
    final int yOffsetPlayerInventory = (inventoryRows - 4) * 18;

    IntStream.range(0, inventoryRows).forEach((row) -> {
      IntStream.range(0, inventoryCols).forEach((col) -> {
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
      final int yPos = 161 + yOffsetPlayerInventory;

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
        final int yPos = row * 18 + 103 + yOffsetPlayerInventory;

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

  // @Override
  // public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
  // {
  // ItemStack itemstack = ItemStack.EMPTY;
  // Slot slot = this.inventorySlots.get(index);

  // if (slot != null && slot.getHasStack())
  // {
  // ItemStack itemstack1 = slot.getStack();
  // itemstack = itemstack1.copy();

  // if (index < this.numRows * 9)
  // {
  // if (!this.mergeItemStack(itemstack1, this.numRows * 9,
  // this.inventorySlots.size(), true))
  // {
  // return ItemStack.EMPTY;
  // }
  // }
  // else if (!this.mergeItemStack(itemstack1, 0, this.numRows * 9, false))
  // {
  // return ItemStack.EMPTY;
  // }

  // if (itemstack1.isEmpty())
  // {
  // slot.putStack(ItemStack.EMPTY);
  // }
  // else
  // {
  // slot.onSlotChanged();
  // }
  // }

  // return itemstack;
  // }
}
