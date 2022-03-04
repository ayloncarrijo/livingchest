package com.syllient.livingchest.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.syllient.livingchest.container.ChesterContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ChesterGui extends ContainerScreen<ChesterContainer> {
  public ChesterGui(final ChesterContainer container, final PlayerInventory inventory,
      final ITextComponent textComponent) {
    super(container, inventory, textComponent);
  }

  @Override
  protected void renderBg(final MatrixStack stack, final float partialTicks, final int mouseX,
      final int mouseY) {}
}
