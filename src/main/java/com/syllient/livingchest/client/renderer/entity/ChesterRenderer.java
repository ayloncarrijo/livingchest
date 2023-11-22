package com.syllient.livingchest.client.renderer.entity;

import com.syllient.livingchest.client.model.entity.ChesterModel;
import com.syllient.livingchest.world.entity.animal.Chester;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class ChesterRenderer extends GeoEntityRenderer<Chester> {
  public ChesterRenderer(final Context context) {
    super(context, new ChesterModel());
  }
}
