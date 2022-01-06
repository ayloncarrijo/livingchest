package com.syllient.livingchest.client.renderer.entity;

import com.syllient.livingchest.client.model.entity.ModelChester;
import com.syllient.livingchest.entity.EntityChester;

import net.minecraft.client.renderer.entity.RenderManager;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RenderChester extends GeoEntityRenderer<EntityChester> {
  public RenderChester(final RenderManager renderManager) {
    super(renderManager, new ModelChester());
    this.shadowSize = 0.6F;

  }
}
