package com.syllient.livingchest.client.model;

import com.syllient.livingchest.saveddata.VirtualChesterSavedData;
import com.syllient.livingchest.tile.EyeBoneTile;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class EyeBoneTileModel extends EyeBoneModel<EyeBoneTile> {
  @Override
  public ResourceLocation getAnimationFileLocation(final EyeBoneTile eyeBone) {
    return ANIMATION;
  }

  @Override
  public ResourceLocation getModelLocation(final EyeBoneTile eyeBone) {
    return MODEL;
  }

  @Override
  public ResourceLocation getTextureLocation(final EyeBoneTile eyeBone) {
    return VirtualChesterSavedData.getInstance(Minecraft.getMinecraft().world)
        .getVirtualChester(eyeBone.getOwnerId()).isDead() ? TEXTURE_CLOSED : TEXTURE_OPENED;
  }
}
