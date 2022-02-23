package com.syllient.livingchest.client.renderer.block;

import com.syllient.livingchest.client.model.block.EyeBoneBlockModel;
import com.syllient.livingchest.tile.EyeBoneTile;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class EyeBoneBlockRenderer extends GeoBlockRenderer<EyeBoneTile> {
  public EyeBoneBlockRenderer() {
    super(new EyeBoneBlockModel());
  }

  @Override
  public void renderEarly(final EyeBoneTile animatable, final float ticks, final float red,
      final float green, final float blue, final float partialTicks) {
    // GlStateManager.scale(5f, 5f, 5f);
  }
}
