package com.syllient.livingchest.registry;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.tile.EyeBoneTile;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber(modid = LivingChest.MOD_ID)
public class TileRegistry {
  @SubscribeEvent
  public static void initialize(final RegistryEvent.Register<Block> event) {
    GameRegistry.registerTileEntity(EyeBoneTile.class,
        new ResourceLocation(LivingChest.MOD_ID, "eye_bone"));
  }
}
