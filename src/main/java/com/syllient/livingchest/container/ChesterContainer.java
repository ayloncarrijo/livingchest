package com.syllient.livingchest.container;

import java.util.stream.IntStream;
import com.syllient.livingchest.entity.ChesterEntity;
import com.syllient.livingchest.eventhandler.registry.ContainerRegistry;
import com.syllient.livingchest.inventory.ChesterInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.SlotItemHandler;

public class ChesterContainer extends Container {
  public final ChesterInventory inventory;
  public final ChesterEntity chester;
  public final int cols;
  public final int rows;
  public final int offsetX;
  public final int offsetY;

  public ChesterContainer(final int windowId, final PlayerInventory inventory,
      final PacketBuffer extraData) {
    this(windowId, inventory.player,
        (ChesterEntity) inventory.player.level.getEntity(extraData.readInt()));
  }

  public ChesterContainer(final int windowId, final PlayerEntity player,
      final ChesterEntity chester) {
    super(ContainerRegistry.CHESTER, windowId);
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

        this.addSlot(new SlotItemHandler(this.inventory, index, posX, posY));
      });
    });

    IntStream.range(0, 3).forEach((row) -> {
      IntStream.range(0, 9).forEach((col) -> {
        final int index = row * 9 + col + 9;
        final int posX = col * 18 + this.offsetX;
        final int posY = row * 18 + 103 + (this.rows - 4) * 18;

        this.addSlot(new Slot(player.inventory, index, posX, posY));
      });
    });

    IntStream.range(0, 9).forEach((col) -> {
      final int index = col;
      final int posX = col * 18 + this.offsetX;
      final int posY = 161 + (this.rows - 4) * 18;

      this.addSlot(new Slot(player.inventory, index, posX, posY));
    });
  }

  @Override
  public boolean stillValid(final PlayerEntity player) {
    return this.chester.isAlive() && this.chester.closerThan(player, 8);
  }

  @Override
  public void removed(final PlayerEntity player) {
    super.removed(player);
    this.inventory.handleInventoryClosure(player);
  }

  @Override
  public ItemStack quickMoveStack(final PlayerEntity player, final int index) {
    ItemStack itemStack = ItemStack.EMPTY;
    final Slot invSlot = this.slots.get(index);

    if (invSlot != null && invSlot.hasItem()) {
      final ItemStack itemStackInSlot = invSlot.getItem();
      itemStack = itemStackInSlot.copy();

      if (index < this.inventory.getSlots()) {
        if (!this.moveItemStackTo(itemStackInSlot, this.inventory.getSlots(), this.slots.size(),
            true)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.moveItemStackTo(itemStackInSlot, 0, this.inventory.getSlots(), false)) {
        return ItemStack.EMPTY;
      }

      if (itemStackInSlot.isEmpty()) {
        invSlot.set(ItemStack.EMPTY);
      } else {
        invSlot.setChanged();
      }
    }

    return itemStack;
  }
}
