package com.syllient.livingchest.client.model;

import com.syllient.livingchest.item.EyeBoneItem;
import net.minecraft.util.ResourceLocation;

public class EyeBoneItemModel extends EyeBoneModel<EyeBoneItem> {
  @Override
  public ResourceLocation getAnimationFileLocation(final EyeBoneItem eyeBone) {
    return ANIMATION;
  }

  @Override
  public ResourceLocation getModelLocation(final EyeBoneItem eyeBone) {
    return MODEL;
  }

  @Override
  public ResourceLocation getTextureLocation(final EyeBoneItem eyeBone) {
    return TEXTURE_OPENED;
  }
}
