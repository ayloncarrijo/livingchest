package com.syllient.livingchest.eventhandler.registry;

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
    event.getRegistry().registerAll(createEntry("chester",
        EntityEntryBuilder.create().entity(ChesterEntity.class).tracker(128, 3, false)));
  }

  private static EntityEntry createEntry(final String name,
      final EntityEntryBuilder<? extends Entity> builder) {
    return builder.id(name, id++).name(LivingChest.MOD_ID + "." + name).build();
  }
}
