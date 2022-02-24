package com.syllient.livingchest.eventhandler.client;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.eventhandler.registry.ItemRegistry;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = LivingChest.MOD_ID, value = Side.CLIENT)
public class ClientEventHandler {
  @SubscribeEvent
  public static void onModelRegistry(final ModelRegistryEvent event) {
    ModelLoader.setCustomModelResourceLocation(ItemRegistry.EYE_BONE, 0,
        new ModelResourceLocation(ItemRegistry.EYE_BONE.getRegistryName(), "inventory"));
  }
}
