package com.syllient.livingchest.registry;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.block.EyeBoneBlock;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = LivingChest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(LivingChest.MOD_ID)
public class BlockRegistry {
  public static final Block EYE_BONE = null;

  @SubscribeEvent
  public static void initialize(final RegistryEvent.Register<Block> event) {
    event.getRegistry().registerAll(buildEntry(new EyeBoneBlock(), "eye_bone"));
  }

  private static Block buildEntry(final Block block, final String name) {
    return block.setRegistryName(name);
  }
}
