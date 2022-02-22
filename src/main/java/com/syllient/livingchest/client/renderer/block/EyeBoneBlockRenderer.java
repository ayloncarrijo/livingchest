package com.syllient.livingchest.client.renderer.block;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.syllient.livingchest.client.model.block.EyeBoneBlockModel;
import com.syllient.livingchest.tile.EyeBoneTile;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class EyeBoneBlockRenderer extends GeoBlockRenderer<EyeBoneTile> {
  public EyeBoneBlockRenderer(final TileEntityRendererDispatcher rendererDispatcher) {
    super(rendererDispatcher, new EyeBoneBlockModel());
  }

  @Override
  public RenderType getRenderType(final EyeBoneTile animatable, final float partialTicks,
      final MatrixStack stack, final IRenderTypeBuffer renderTypeBuffer,
      final IVertexBuilder vertexBuilder, final int packedLightIn,
      final ResourceLocation textureLocation) {
    return RenderType.entityTranslucent(getTextureLocation(animatable));
  }
}
