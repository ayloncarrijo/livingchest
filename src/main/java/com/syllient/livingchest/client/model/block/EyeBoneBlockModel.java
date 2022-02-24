package com.syllient.livingchest.client.model.block;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.saveddata.VirtualChesterSavedData;
import com.syllient.livingchest.tile.EyeBoneTile;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class EyeBoneBlockModel extends AnimatedGeoModel<EyeBoneTile> {
  public static final ResourceLocation ANIMATION =
      new ResourceLocation(LivingChest.MOD_ID, "animations/block/eye_bone.json");

  public static final ResourceLocation MODEL =
      new ResourceLocation(LivingChest.MOD_ID, "geo/block/eye_bone.json");

  public static final ResourceLocation TEXTURE_OPEN =
      new ResourceLocation(LivingChest.MOD_ID, "textures/block/eye_bone.open.png");

  public static final ResourceLocation TEXTURE_CLOSED =
      new ResourceLocation(LivingChest.MOD_ID, "textures/block/eye_bone.closed.png");

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
    if (eyeBone.getOwnerId() == null) {
      return TEXTURE_OPEN;
    }

    return VirtualChesterSavedData.getInstance(Minecraft.getMinecraft().world)
        .getVirtualChester(eyeBone.getOwnerId()).isDead() ? TEXTURE_CLOSED : TEXTURE_OPEN;
  }
}
