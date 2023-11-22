package com.syllient.livingchest.world.entity;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.world.entity.animal.Chester;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = LivingChest.MOD_ID, bus = Bus.MOD)
public class EntityTypes {
  private static final DeferredRegister<EntityType<?>> REGISTRY =
      DeferredRegister.create(ForgeRegistries.ENTITIES, LivingChest.MOD_ID);

  public static final RegistryObject<EntityType<Chester>> CHESTER =
      register("chester", EntityType.Builder.of(Chester::new, MobCategory.CREATURE).sized(1F, 1F)
          .clientTrackingRange(10));

  private static <T extends Entity> RegistryObject<EntityType<T>> register(final String name,
      final EntityType.Builder<T> builder) {
    return REGISTRY.register(name,
        () -> builder.build(new ResourceLocation(LivingChest.MOD_ID, name).toString()));
  }

  @SubscribeEvent
  public static void createAttributes(final EntityAttributeCreationEvent event) {
    event.put(CHESTER.get(), Chester.createAttributes().build());
  }

  public static void registerRegistry() {
    REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
  }
}
