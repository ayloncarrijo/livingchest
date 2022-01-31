package com.syllient.livingchest.client.model;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.item.EyeBoneItem;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class EyeBoneItemModel extends AnimatedGeoModel<EyeBoneItem> {
  @Override
  public ResourceLocation getAnimationFileLocation(final EyeBoneItem eyeBone) {
    return null;
  }

  @Override
  public ResourceLocation getModelLocation(final EyeBoneItem eyeBone) {
    return new ResourceLocation(LivingChest.MOD_ID, "geo/eye_bone.geo.json");
  }

  @Override
  public ResourceLocation getTextureLocation(final EyeBoneItem eyeBone) {
    return new ResourceLocation(LivingChest.MOD_ID, "textures/eye_bone_opened.png");
  }
}
