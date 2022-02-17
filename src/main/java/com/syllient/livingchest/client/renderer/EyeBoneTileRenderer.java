package com.syllient.livingchest.client.renderer;

import com.syllient.livingchest.client.model.EyeBoneTileModel;
import com.syllient.livingchest.tile.EyeBoneTile;
import net.minecraft.client.renderer.GlStateManager;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class EyeBoneTileRenderer extends GeoBlockRenderer<EyeBoneTile> {
  public EyeBoneTileRenderer() {
    super(new EyeBoneTileModel());
  }

  @Override
  public void renderEarly(final EyeBoneTile animatable, final float ticks, final float red,
      final float green, final float blue, final float partialTicks) {
    GlStateManager.scale(5f, 5f, 5f);
  }
}
