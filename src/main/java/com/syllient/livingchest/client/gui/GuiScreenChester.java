package com.syllient.livingchest.client.gui;

import com.syllient.livingchest.entity.EntityChester;
import com.syllient.livingchest.inventory.ContainerChester;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;

public class GuiScreenChester extends GuiContainer {
  public GuiScreenChester(final EntityChester chester) {
    super(new ContainerChester(Minecraft.getMinecraft().player, chester));
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(
      final float partialTicks,
      final int mouseX,
      final int mouseY) {
  }
}
