package com.syllient.livingchest.client.model;

import com.syllient.livingchest.tileentity.EyeBoneTileEntity;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class EyeBoneTileEntityModel extends AnimatedGeoModel<EyeBoneTileEntity> {
  @Override
  public ResourceLocation getAnimationFileLocation(final EyeBoneTileEntity eyeBone) {
    return EyeBoneItemModel.ANIMATION;
  }

  @Override
  public ResourceLocation getModelLocation(final EyeBoneTileEntity eyeBone) {
    return EyeBoneItemModel.MODEL;
  }

  @Override
  public ResourceLocation getTextureLocation(final EyeBoneTileEntity eyeBone) {
    return EyeBoneItemModel.TEXTURE_OPENED;
  }
}
