package com.syllient.livingchest.registry;

import com.syllient.livingchest.LivingChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@Mod.EventBusSubscriber(modid = LivingChest.MOD_ID)
public class SoundRegistry {
  @SubscribeEvent
  public static void initialize(final RegistryEvent.Register<SoundEvent> event) {
    event.getRegistry().registerAll(buildEntry("entity.chester.idle"),
        buildEntry("entity.chester.jump"), buildEntry("entity.chester.hurt"),
        buildEntry("entity.chester.death"), buildEntry("entity.chester.open_mouth"),
        buildEntry("entity.chester.close_mouth"));
  }

  private static SoundEvent buildEntry(final String name) {
    return new SoundEvent(new ResourceLocation(LivingChest.MOD_ID, name)).setRegistryName(name);
  }

  public static class Entity {
    @ObjectHolder(LivingChest.MOD_ID)
    public static class Chester {
      @ObjectHolder("entity.chester.idle")
      public static final SoundEvent IDLE = null;

      @ObjectHolder("entity.chester.jump")
      public static final SoundEvent JUMP = null;

      @ObjectHolder("entity.chester.hurt")
      public static final SoundEvent HURT = null;

      @ObjectHolder("entity.chester.death")
      public static final SoundEvent DEATH = null;

      @ObjectHolder("entity.chester.open_mouth")
      public static final SoundEvent OPEN_MOUTH = null;

      @ObjectHolder("entity.chester.close_mouth")
      public static final SoundEvent CLOSE_MOUTH = null;
    }
  }
}
