package com.syllient.livingchest.world.level.block;

import com.syllient.livingchest.LivingChest;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Blocks {
  private static final DeferredRegister<Block> REGISTRY =
      DeferredRegister.create(ForgeRegistries.BLOCKS, LivingChest.MOD_ID);

  public static final RegistryObject<Block> EYE_BONE = REGISTRY.register("eye_bone",
      () -> new EyeBoneBlock(BlockBehaviour.Properties.of(Material.STONE)));

  public static void registerRegistry() {
    REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
  }
}
