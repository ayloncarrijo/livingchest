package com.syllient.livingchest.registry;

import com.syllient.livingchest.LivingChest;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class SoundRegistry {
  public static class ChesterEntity {
    public static final SoundEvent OPEN_MOUTH = SoundRegistry.createSoundEvent("chester.open_mouth");
    public static final SoundEvent CLOSE_MOUTH = SoundRegistry.createSoundEvent("chester.close_mouth");
  }

  private static SoundEvent createSoundEvent(final String name) {
    final ResourceLocation location = new ResourceLocation(LivingChest.MOD_ID, name);

    return new SoundEvent(location).setRegistryName(location);
  }
}
