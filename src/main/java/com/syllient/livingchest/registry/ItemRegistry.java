package com.syllient.livingchest.registry;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.item.EyeBoneItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = LivingChest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(LivingChest.MOD_ID)
public class ItemRegistry {
  public static final Item EYE_BONE = null;

  @SubscribeEvent
  public static void initialize(final RegistryEvent.Register<Item> event) {
    event.getRegistry().registerAll(buildEntry("eye_bone", new EyeBoneItem()));
  }

  private static Item buildEntry(final String name, final Item item) {
    return item.setRegistryName(name);
  }
}
