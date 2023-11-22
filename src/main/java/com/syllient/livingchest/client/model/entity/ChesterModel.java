package com.syllient.livingchest.client.model.entity;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.world.entity.animal.Chester;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ChesterModel extends AnimatedGeoModel<Chester> {
  public static final ResourceLocation ANIMATION_LOCATION =
      new ResourceLocation(LivingChest.MOD_ID, "animations/entity/chester/chester.animation.json");

  public static final ResourceLocation MODEL_LOCATION =
      new ResourceLocation(LivingChest.MOD_ID, "geo/entity/chester/chester.geo.json");

  public static final ResourceLocation TEXTURE_LOCATION =
      new ResourceLocation(LivingChest.MOD_ID, "textures/entity/chester/chester.png");

  @Override
  public ResourceLocation getAnimationFileLocation(final Chester animatable) {
    return ANIMATION_LOCATION;
  }

  @Override
  public ResourceLocation getModelLocation(final Chester animatable) {
    return MODEL_LOCATION;
  }

  @Override
  public ResourceLocation getTextureLocation(final Chester animatable) {
    return TEXTURE_LOCATION;
  }
}
