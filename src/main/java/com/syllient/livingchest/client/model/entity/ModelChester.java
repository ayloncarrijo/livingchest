package com.syllient.livingchest.client.model.entity;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.entity.EntityChester;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ModelChester extends AnimatedGeoModel<EntityChester> {
  @Override
  public ResourceLocation getAnimationFileLocation(EntityChester animatable) {
    return new ResourceLocation(LivingChest.MODID, "animations/chester.animation.json");
  }

  @Override
  public ResourceLocation getModelLocation(EntityChester object) {
    return new ResourceLocation(LivingChest.MODID, "geo/chester.geo.json");
  }

  @Override
  public ResourceLocation getTextureLocation(EntityChester object) {
    return new ResourceLocation(LivingChest.MODID, "textures/chester.png");
  }
}
