package com.syllient.livingchest.client.model;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.entity.ChesterEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ChesterModel extends AnimatedGeoModel<ChesterEntity> {
  private static final ResourceLocation ANIMATION =
      new ResourceLocation(LivingChest.MOD_ID, "animations/chester.json");

  private static final ResourceLocation MODEL =
      new ResourceLocation(LivingChest.MOD_ID, "geo/chester.json");

  private static final ResourceLocation TEXTURE =
      new ResourceLocation(LivingChest.MOD_ID, "textures/chester.png");

  @Override
  public ResourceLocation getAnimationFileLocation(final ChesterEntity chester) {
    return ANIMATION;
  }

  @Override
  public ResourceLocation getModelLocation(final ChesterEntity chester) {
    return MODEL;
  }

  @Override
  public ResourceLocation getTextureLocation(final ChesterEntity chester) {
    return TEXTURE;
  }
}
