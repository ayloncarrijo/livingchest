package com.syllient.livingchest;

import com.syllient.livingchest.client.renderer.block.EyeBoneBlockRenderer;
import com.syllient.livingchest.client.renderer.entity.ChesterRenderer;
import com.syllient.livingchest.eventhandler.registry.EntityRegistry;
import com.syllient.livingchest.eventhandler.registry.TileRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import software.bernie.geckolib3.GeckoLib;

@Mod(LivingChest.MOD_ID)
@Mod.EventBusSubscriber(modid = LivingChest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LivingChest {
  public static final String MOD_ID = "livingchest";

  public LivingChest() {
    GeckoLib.initialize();
  }

  @SubscribeEvent
  public static void onCommonSetup(final FMLCommonSetupEvent event) {}

  @SubscribeEvent
  public static void onClientSetup(final FMLClientSetupEvent event) {
    RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.CHESTER, ChesterRenderer::new);

    ClientRegistry.bindTileEntityRenderer(TileRegistry.EYE_BONE, EyeBoneBlockRenderer::new);
  }

  @SubscribeEvent
  public static void onServerSetup(final FMLDedicatedServerSetupEvent event) {}
}
