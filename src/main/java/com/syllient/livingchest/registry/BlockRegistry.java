package com.syllient.livingchest.registry;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.block.EyeBoneBlock;
import net.minecraft.block.Block;
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
    event.getRegistry().registerAll(buildEntry("eye_bone", new EyeBoneBlock()));
  }

  private static Block buildEntry(final String name, final Block block) {
    return block.setRegistryName(name).setUnlocalizedName(LivingChest.MOD_ID + "." + name);
  }
}
