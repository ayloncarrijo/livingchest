package com.syllient.livingchest;

import com.syllient.livingchest.entity.EntityChester;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = LivingChest.MODID, value = { Side.CLIENT, Side.SERVER })
public class EventHandler {
  private static int entityId = 0;

  @SubscribeEvent
  public static void onRegisterEntities(RegistryEvent.Register<EntityEntry> event) {
    event.getRegistry()
        .registerAll(
            EventHandler.createEntityEntry(EntityChester.class, "chester"));
  }

  private static EntityEntry createEntityEntry(Class<? extends Entity> clazz, String name) {
    return EntityEntryBuilder
        .create()
        .entity(clazz)
        .tracker(64, 1, true)
        .name(LivingChest.MODID + "." + name)
        .id(new ResourceLocation(LivingChest.MODID, name), EventHandler.entityId++)
        .egg(0xFF000000, 0xFF000000)
        .build();
  }
}
