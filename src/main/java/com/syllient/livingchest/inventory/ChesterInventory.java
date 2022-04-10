package com.syllient.livingchest.inventory;

import com.syllient.livingchest.entity.ChesterEntity;
import com.syllient.livingchest.eventhandler.registry.ItemRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ChesterInventory extends ItemStackHandler {
  private final ChesterEntity chester;
  private int openCount = 0;

  public ChesterInventory(final ChesterEntity chester, final int size) {
    super(size);
    this.chester = chester;
  }

  @Override
  public boolean isItemValid(final int slot, final ItemStack stack) {
    return stack.getItem() != ItemRegistry.EYE_BONE;
  }

  public void handleInventoryOpening(final EntityPlayer player) {
    if (player.world.isRemote || player.isSpectator()) {
      return;
    }

    if (++this.openCount == 1) {
      this.chester.openMouth();
    }
  }

  public void handleInventoryClosure(final EntityPlayer player) {
    if (player.world.isRemote || player.isSpectator()) {
      return;
    }

    if (--this.openCount <= 0) {
      this.chester.closeMouth();
    }
  }
}
