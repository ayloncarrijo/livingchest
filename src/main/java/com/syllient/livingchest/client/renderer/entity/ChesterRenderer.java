package com.syllient.livingchest.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.syllient.livingchest.client.model.entity.ChesterModel;
import com.syllient.livingchest.entity.ChesterEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class ChesterRenderer extends GeoEntityRenderer<ChesterEntity> {
  public ChesterRenderer(final EntityRendererManager rendererManager) {
    super(rendererManager, new ChesterModel());
  }

  @Override
  public RenderType getRenderType(final ChesterEntity animatable, final float partialTicks,
      final MatrixStack stack, final IRenderTypeBuffer renderTypeBuffer,
      final IVertexBuilder vertexBuilder, final int packedLightIn,
      final ResourceLocation textureLocation) {
    return RenderType.entityTranslucent(getTextureLocation(animatable));
  }
}
