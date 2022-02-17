package com.syllient.livingchest.registry;

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
    event.getRegistry()
        .registerAll(buildEntry(new EyeBoneItem(BlockRegistry.EYE_BONE), "eye_bone"));
  }

  private static Item buildEntry(final Item item, final String name) {
    return item.setRegistryName(name).setUnlocalizedName(LivingChest.MOD_ID + "." + name);
  }
}
