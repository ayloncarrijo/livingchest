package com.syllient.livingchest.client.model;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.item.EyeBoneItem;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class EyeBoneItemModel extends AnimatedGeoModel<EyeBoneItem> {
  public static final ResourceLocation ANIMATION = null;
  public static final ResourceLocation MODEL = new ResourceLocation(LivingChest.MOD_ID, "geo/eye_bone.geo.json");
  public static final ResourceLocation TEXTURE_OPENED = new ResourceLocation(LivingChest.MOD_ID,
      "textures/eye_bone_opened.png");
  public static final ResourceLocation TEXTURE_CLOSED = new ResourceLocation(LivingChest.MOD_ID,
      "textures/eye_bone_closed.png");

  @Override
  public ResourceLocation getAnimationFileLocation(final EyeBoneItem eyeBone) {
    return EyeBoneItemModel.ANIMATION;
  }

  @Override
  public ResourceLocation getModelLocation(final EyeBoneItem eyeBone) {
    return EyeBoneItemModel.MODEL;
  }

  @Override
  public ResourceLocation getTextureLocation(final EyeBoneItem eyeBone) {
    return EyeBoneItemModel.TEXTURE_OPENED;
  }
}
