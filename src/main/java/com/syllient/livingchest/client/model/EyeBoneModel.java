package com.syllient.livingchest.client.model;

import com.syllient.livingchest.LivingChest;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public abstract class EyeBoneModel<T extends IAnimatable> extends AnimatedGeoModel<T> {
  protected static final ResourceLocation ANIMATION =
      new ResourceLocation(LivingChest.MOD_ID, "animations/eye_bone.json");

  protected static final ResourceLocation MODEL =
      new ResourceLocation(LivingChest.MOD_ID, "geo/eye_bone.json");

  protected static final ResourceLocation TEXTURE_OPENED =
      new ResourceLocation(LivingChest.MOD_ID, "textures/eye_bone_opened.png");

  protected static final ResourceLocation TEXTURE_CLOSED =
      new ResourceLocation(LivingChest.MOD_ID, "textures/eye_bone_closed.png");
}
