package com.syllient.livingchest;

import com.syllient.livingchest.entity.ChesterEntity;
import com.syllient.livingchest.registry.SoundRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = LivingChest.MOD_ID, value = { Side.CLIENT, Side.SERVER })
public class EventHandler {
  private static int entityId = 0;

  @SubscribeEvent
  public static void onRegisterEntity(final RegistryEvent.Register<EntityEntry> event) {
    event.getRegistry().registerAll(
        EventHandler.createEntityEntry(ChesterEntity.class, "chester"));
  }

  @SubscribeEvent
  public static void onRegisterSound(final RegistryEvent.Register<SoundEvent> event) {
    event.getRegistry().registerAll(
        SoundRegistry.ChesterEntity.OPEN_MOUTH,
        SoundRegistry.ChesterEntity.CLOSE_MOUTH);
  }

  private static EntityEntry createEntityEntry(final Class<? extends Entity> clazz, final String name) {
    return EntityEntryBuilder
        .create()
        .entity(clazz)
        .tracker(64, 1, true)
        .name(LivingChest.MOD_ID + "." + name)
        .id(new ResourceLocation(LivingChest.MOD_ID, name), EventHandler.entityId++)
        .egg(0xFF000000, 0xFF000000)
        .build();
  }
}
