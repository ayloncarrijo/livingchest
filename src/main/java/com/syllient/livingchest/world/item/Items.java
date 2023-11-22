package com.syllient.livingchest.world.item;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.world.level.block.Blocks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Items {
  private static final DeferredRegister<Item> REGISTRY =
      DeferredRegister.create(ForgeRegistries.ITEMS, LivingChest.MOD_ID);

  public static final RegistryObject<Item> EYE_BONE =
      REGISTRY.register("eye_bone", () -> new EyeBoneItem(Blocks.EYE_BONE.get(),
          new Item.Properties().tab(CreativeModeTab.TAB_TOOLS)));

  public static void registerRegistry() {
    REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
  }
}
