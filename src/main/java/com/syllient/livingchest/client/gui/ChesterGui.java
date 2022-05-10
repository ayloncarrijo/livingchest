package com.syllient.livingchest.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.container.ChesterContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ChesterGui extends ContainerScreen<ChesterContainer> {
  private static final ResourceLocation TEXTURE =
      new ResourceLocation(LivingChest.MOD_ID, "textures/gui/chester.png");

  private final ChesterContainer container;
  protected int textColor;

  public ChesterGui(final ChesterContainer container, final PlayerInventory inventory,
      final ITextComponent textComponent) {
    super(container, inventory, textComponent);
    this.container = container;
    this.passEvents = false;
    this.imageWidth = 176;
    this.imageHeight = 114 + this.container.rows * 18;
    this.titleLabelX = 8;
    this.titleLabelY = 6;
    this.inventoryLabelX = 8;
    this.inventoryLabelY = this.imageHeight - 94;
    this.textColor = 13876910;
  }

  @Override
  public void render(final MatrixStack stack, final int mouseX, final int mouseY,
      final float partialTicks) {
    this.renderBackground(stack);
    super.render(stack, mouseX, mouseY, partialTicks);
    this.renderTooltip(stack, mouseX, mouseY);
  }

  @Override
  @SuppressWarnings("deprecation")
  protected void renderBg(final MatrixStack stack, final float partialTicks, final int mouseX,
      final int mouseY) {
    Minecraft.getInstance().getTextureManager().bind(TEXTURE);
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.blit(stack, this.getGuiLeft(), this.getGuiTop(), 0, 0, this.imageWidth,
        this.container.rows * 18 + 17);
    this.blit(stack, this.getGuiLeft(), this.getGuiTop() + this.container.rows * 18 + 17, 0, 126,
        this.imageWidth, 96);
  }

  @Override
  protected void renderLabels(final MatrixStack stack, final int mouseX, final int mouseY) {
    this.font.draw(stack, this.title, this.titleLabelX, this.titleLabelY, this.textColor);
    this.font.draw(stack, this.inventory.getDisplayName(), this.inventoryLabelX,
        this.inventoryLabelY, this.textColor);
  }
}
