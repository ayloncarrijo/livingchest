package com.syllient.livingchest.client.gui;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.container.ChesterContainer;
import com.syllient.livingchest.entity.ChesterEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ChesterGui extends GuiContainer {
  private static final ResourceLocation TEXTURE = new ResourceLocation(
      LivingChest.MOD_ID,
      "textures/gui/chester.png");

  private final int invCols;
  private final int invRows;

  public ChesterGui(final ChesterEntity chester) {
    super(new ChesterContainer(Minecraft.getMinecraft().player, chester));
    this.invCols = 9;
    this.invRows = chester.getInventory().getSlots() / this.invCols;
    this.ySize = 114 + this.invRows * 18;
  }

  @Override
  public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
    this.drawDefaultBackground();
    super.drawScreen(mouseX, mouseY, partialTicks);
    this.renderHoveredToolTip(mouseX, mouseY);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(
      final float partialTicks,
      final int mouseX,
      final int mouseY) {
    Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    this.drawTexturedModalRect(
        this.guiLeft,
        this.guiTop,
        0,
        0,
        this.xSize,
        this.invRows * 18 + 17);

    this.drawTexturedModalRect(
        this.guiLeft,
        this.guiTop + this.invRows * 18 + 17,
        0,
        126,
        this.xSize,
        96);
  }

  @Override
  protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
    final int intColor = 15191721;

    this.fontRenderer.drawString(
        "Chester",
        8,
        6,
        intColor);

    this.fontRenderer.drawString(
        "Inventory",
        8,
        this.ySize - 96 + 2,
        intColor);
  }
}
