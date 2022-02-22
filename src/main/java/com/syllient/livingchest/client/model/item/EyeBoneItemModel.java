package com.syllient.livingchest.client.model.item;

import com.syllient.livingchest.client.model.block.EyeBoneBlockModel;
import com.syllient.livingchest.item.EyeBoneItem;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class EyeBoneItemModel extends AnimatedGeoModel<EyeBoneItem> {
  private static final ResourceLocation ANIMATION = EyeBoneBlockModel.ANIMATION;
  private static final ResourceLocation MODEL = EyeBoneBlockModel.MODEL;
  private static final ResourceLocation TEXTURE_OPENED = EyeBoneBlockModel.TEXTURE_OPENED;
  private static final ResourceLocation TEXTURE_CLOSED = EyeBoneBlockModel.TEXTURE_CLOSED;

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
    final boolean isAlive = true;
    return isAlive ? TEXTURE_OPENED : TEXTURE_CLOSED;
  }
}
