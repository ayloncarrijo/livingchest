package com.syllient.livingchest.inventory;

import com.syllient.livingchest.entity.ChesterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.ItemStackHandler;

public class ChesterInventory extends ItemStackHandler {
  private final ChesterEntity chester;
  private int openCount = 0;

  public ChesterInventory(final ChesterEntity chester, final int size) {
    super(size);
    this.chester = chester;
  }

  public void handleInventoryOpening(final PlayerEntity player) {
    if (player.level.isClientSide || player.isSpectator()) {
      return;
    }

    if (++this.openCount == 1) {
      this.chester.openMouth();
    }
  }

  public void handleInventoryClosure(final PlayerEntity player) {
    if (player.level.isClientSide || player.isSpectator()) {
      return;
    }

    if (--this.openCount <= 0) {
      this.chester.closeMouth();
    }
  }
}
