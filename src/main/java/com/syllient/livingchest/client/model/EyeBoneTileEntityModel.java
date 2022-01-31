package com.syllient.livingchest.client.model;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.tileentity.EyeBoneTileEntity;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class EyeBoneTileEntityModel extends AnimatedGeoModel<EyeBoneTileEntity> {
  @Override
  public ResourceLocation getAnimationFileLocation(final EyeBoneTileEntity eyeBone) {
    return null;
  }

  @Override
  public ResourceLocation getModelLocation(final EyeBoneTileEntity eyeBone) {
    return new ResourceLocation(LivingChest.MOD_ID, "geo/eye_bone.geo.json");
  }

  @Override
  public ResourceLocation getTextureLocation(final EyeBoneTileEntity eyeBone) {
    return new ResourceLocation(LivingChest.MOD_ID, "textures/eye_bone.opened.png");
  }
}
