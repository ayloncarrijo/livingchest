package com.syllient.livingchest.registry;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.entity.ChesterEntity;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

@Mod.EventBusSubscriber(modid = LivingChest.MOD_ID)
public class EntityRegistry {
  private static int id = 0;

  @SubscribeEvent
  public static void initialize(final RegistryEvent.Register<EntityEntry> event) {
    event.getRegistry().registerAll(buildEntry(ChesterEntity.class, "chester"));
  }

  private static EntityEntry buildEntry(final Class<? extends Entity> clazz, final String name) {
    return EntityEntryBuilder.create().entity(clazz).id(name, id++)
        .name(LivingChest.MOD_ID + "." + name).tracker(64, 1, true).build();
  }
}
