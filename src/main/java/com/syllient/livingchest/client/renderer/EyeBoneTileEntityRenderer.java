package com.syllient.livingchest.client.renderer;

import com.syllient.livingchest.client.model.EyeBoneTileEntityModel;
import com.syllient.livingchest.tileentity.EyeBoneTileEntity;
import net.minecraft.client.renderer.GlStateManager;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class EyeBoneTileEntityRenderer extends GeoBlockRenderer<EyeBoneTileEntity> {
  public EyeBoneTileEntityRenderer() {
    super(new EyeBoneTileEntityModel());
  }

  @Override
  public void renderEarly(final EyeBoneTileEntity animatable, final float ticks, final float red,
      final float green, final float blue, final float partialTicks) {
    GlStateManager.scale(5f, 5f, 5f);
  }
}
