package com.syllient.livingchest.client.gui;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.container.ChesterContainer;
import com.syllient.livingchest.entity.ChesterEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class ChesterGui extends GuiContainer {
  private static final ResourceLocation TEXTURE =
      new ResourceLocation(LivingChest.MOD_ID, "textures/gui/chester.png");

  private final ChesterContainer container;
  protected int titleLabelX;
  protected int titleLabelY;
  protected int inventoryLabelX;
  protected int inventoryLabelY;
  protected int textColor;

  public ChesterGui(final EntityPlayer player, final ChesterEntity chester) {
    super(new ChesterContainer(player, chester));
    this.container = (ChesterContainer) this.inventorySlots;
    this.xSize = 176;
    this.ySize = 114 + this.container.rows * 18;
    this.titleLabelX = 8;
    this.titleLabelY = 6;
    this.inventoryLabelX = 8;
    this.inventoryLabelY = this.ySize - 94;
    this.textColor = 13876910;
  }

  @Override
  public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
    this.drawDefaultBackground();
    super.drawScreen(mouseX, mouseY, partialTicks);
    this.renderHoveredToolTip(mouseX, mouseY);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX,
      final int mouseY) {
    Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize,
        this.container.rows * 18 + 17);
    this.drawTexturedModalRect(this.guiLeft, this.guiTop + this.container.rows * 18 + 17, 0, 126,
        this.xSize, 96);
  }

  @Override
  protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
    this.fontRenderer.drawString("Chester", this.titleLabelX, this.titleLabelY, this.textColor);
    this.fontRenderer.drawString("Inventory", this.inventoryLabelX, this.inventoryLabelY,
        this.textColor);
  }
}
