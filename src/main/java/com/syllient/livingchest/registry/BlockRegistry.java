package com.syllient.livingchest.registry;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.block.EyeBoneBlock;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@Mod.EventBusSubscriber(modid = LivingChest.MOD_ID)
@ObjectHolder(LivingChest.MOD_ID)
public class BlockRegistry {
  public static final Block EYE_BONE = null;

  @SubscribeEvent
  public static void initialize(final RegistryEvent.Register<Block> event) {
    event.getRegistry().registerAll(
        BlockRegistry.buildEntry(new EyeBoneBlock(), "eye_bone"));
  }

  private static Block buildEntry(final Block block, final String name) {
    return block
        .setUnlocalizedName(LivingChest.MOD_ID + "." + name)
        .setRegistryName(new ResourceLocation(LivingChest.MOD_ID, name));
  }
}
