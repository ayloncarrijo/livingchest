package com.syllient.livingchest.world.level.block;

import com.syllient.livingchest.LivingChest;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Blocks {
  private static final DeferredRegister<Block> REGISTRY =
      DeferredRegister.create(ForgeRegistries.BLOCKS, LivingChest.MOD_ID);

  public static final RegistryObject<Block> EYE_BONE = REGISTRY.register("eye_bone",
      () -> new EyeBoneBlock(AbstractBlock.Properties.of(Material.STONE)));

  public static void registerRegistry() {
    REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
  }
}
