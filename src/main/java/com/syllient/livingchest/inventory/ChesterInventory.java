package com.syllient.livingchest.inventory;

import com.syllient.livingchest.entity.ChesterEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.ItemStackHandler;

public class ChesterInventory extends ItemStackHandler {
  private final ChesterEntity chester;
  private int openCount = 0;

  public ChesterInventory(final ChesterEntity chester, final int size) {
    super(size);
    this.chester = chester;
  }

  public void onOpenInventory(final EntityPlayer playerIn) {
    if (playerIn.isSpectator()) {
      return;
    }

    if (this.openCount < 0) {
      this.openCount = 0;
    }

    this.openCount += 1;

    if (this.openCount == 1) {
      this.chester.openMouth();
    }
  }

  public void onCloseInventory(final EntityPlayer playerIn) {
    if (playerIn.isSpectator()) {
      return;
    }

    this.openCount -= 1;

    if (this.openCount <= 0) {
      this.chester.closeMouth();
    }
  }
}
