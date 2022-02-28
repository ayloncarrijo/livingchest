package com.syllient.livingchest.eventhandler.registry;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.entity.ChesterEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
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
    event.getRegistry()
        .registerAll(createEntry("chester",
            EntityType.Builder.of(ChesterEntity::new, EntityClassification.CREATURE)
                .setTrackingRange(128).setUpdateInterval(3).sized(0.7F, 0.85F)));
  }

  @SubscribeEvent
  public static void initializeAttributes(final EntityAttributeCreationEvent event) {
    event.put(CHESTER, MobEntity.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3D)
        .add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.ATTACK_DAMAGE, 2.0D).build());
  }

  private static EntityType<? extends Entity> createEntry(final String name,
      final EntityType.Builder<? extends Entity> builder) {
    return builder.build(name).setRegistryName(name);
  }
}
