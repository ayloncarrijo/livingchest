package com.syllient.livingchest.client.model;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.entity.ChesterEntity;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ChesterModel extends AnimatedGeoModel<ChesterEntity> {
  @Override
  public ResourceLocation getAnimationFileLocation(final ChesterEntity chester) {
    return new ResourceLocation(LivingChest.MOD_ID, "animations/chester.animation.json");
  }

  @Override
  public ResourceLocation getModelLocation(final ChesterEntity chester) {
    return new ResourceLocation(LivingChest.MOD_ID, "geo/chester.geo.json");
  }

  @Override
  public ResourceLocation getTextureLocation(final ChesterEntity chester) {
    return new ResourceLocation(LivingChest.MOD_ID, "textures/chester.png");
  }
}
