package com.syllient.livingchest.eventhandler.registry;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.item.EyeBoneItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@Mod.EventBusSubscriber(modid = LivingChest.MOD_ID)
@ObjectHolder(LivingChest.MOD_ID)
public class ItemRegistry {
  public static final Item EYE_BONE = null;

  @SubscribeEvent
  public static void initialize(final RegistryEvent.Register<Item> event) {
    event.getRegistry().registerAll(createEntry("eye_bone", new EyeBoneItem()));
  }

  private static Item createEntry(final String name, final Item item) {
    return item.setRegistryName(name).setUnlocalizedName(LivingChest.MOD_ID + "." + name);
  }
}
