package com.syllient.livingchest.client.model;

import com.syllient.livingchest.tileentity.EyeBoneTileEntity;
import net.minecraft.util.ResourceLocation;

public class EyeBoneTileEntityModel extends EyeBoneModel<EyeBoneTileEntity> {
  @Override
  public ResourceLocation getAnimationFileLocation(final EyeBoneTileEntity eyeBone) {
    return ANIMATION;
  }

  @Override
  public ResourceLocation getModelLocation(final EyeBoneTileEntity eyeBone) {
    return MODEL;
  }

  @Override
  public ResourceLocation getTextureLocation(final EyeBoneTileEntity eyeBone) {
    return TEXTURE_OPENED;
  }
}
