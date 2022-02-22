package com.syllient.livingchest.registry;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.entity.ChesterEntity;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = LivingChest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(LivingChest.MOD_ID)
public class EntityRegistry {
  public static final EntityType<ChesterEntity> CHESTER = null;

  @SubscribeEvent
  public static void initializeRegistries(final RegistryEvent.Register<EntityType<?>> event) {
    event.getRegistry().registerAll(ChesterEntity.buildRegistry());
  }

  @SubscribeEvent
  public static void initializeAttributes(final EntityAttributeCreationEvent event) {
    event.put(CHESTER, ChesterEntity.buildAttributes());
  }
}
