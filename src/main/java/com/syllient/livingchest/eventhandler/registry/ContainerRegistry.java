package com.syllient.livingchest.eventhandler.registry;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.container.ChesterContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = LivingChest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(LivingChest.MOD_ID)
public class ContainerRegistry {
  public static final ContainerType<ChesterContainer> CHESTER = null;

  @SubscribeEvent
  public static void initialize(final RegistryEvent.Register<ContainerType<?>> event) {
    event.getRegistry().registerAll(createEntry("chester", ChesterContainer::new));
  }

  private static ContainerType<?> createEntry(final String name,
      final IContainerFactory<? extends Container> factory) {
    return IForgeContainerType.create(factory).setRegistryName(name);
  }
}
