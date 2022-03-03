package com.syllient.livingchest.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

public class ChesterContainer extends Container {
  public ChesterContainer(final ContainerType<?> type, final int windowId) {
    // super(ContainerRegistry.CHESTER, id);
    super(type, windowId);
  }

  // public ChesterContainer(final int windowId, final PlayerEntity player,
  // final ChesterEntity chester) {
  // super(ContainerRegistry.CHESTER, windowId);
  // }

  @Override
  public boolean stillValid(final PlayerEntity player) {
    return false;
  }
}
