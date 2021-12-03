package com.syllient.livingchest.client.model.entity;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.entity.EntityChester;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

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

  @Override
  public void setLivingAnimations(EntityChester entity, Integer uniqueID, AnimationEvent customPredicate) {
    super.setLivingAnimations(entity, uniqueID, customPredicate);

    EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
    IBone root = this.getAnimationProcessor().getBone("root");

    root.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
  }
}
