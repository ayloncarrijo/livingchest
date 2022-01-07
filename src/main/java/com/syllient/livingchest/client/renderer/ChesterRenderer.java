package com.syllient.livingchest.client.renderer;

import com.syllient.livingchest.client.model.ChesterModel;
import com.syllient.livingchest.entity.ChesterEntity;

import net.minecraft.client.renderer.entity.RenderManager;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class ChesterRenderer extends GeoEntityRenderer<ChesterEntity> {
  public ChesterRenderer(final RenderManager renderManager) {
    super(renderManager, new ChesterModel());
    this.shadowSize = 0.6F;

  }
}
