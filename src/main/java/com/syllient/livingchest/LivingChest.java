package com.syllient.livingchest;

import com.syllient.livingchest.client.renderer.entity.ChesterRenderer;
import com.syllient.livingchest.world.entity.EntityTypes;
import com.syllient.livingchest.world.item.Items;
import com.syllient.livingchest.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import software.bernie.geckolib3.GeckoLib;

@Mod(LivingChest.MOD_ID)
@Mod.EventBusSubscriber(modid = LivingChest.MOD_ID, bus = Bus.MOD)
public class LivingChest {
  public static final String MOD_ID = "livingchest";

  public LivingChest() {
    GeckoLib.initialize();
    Items.registerRegistry();
    Blocks.registerRegistry();
    EntityTypes.registerRegistry();
  }

  @SubscribeEvent
  public static void initialize(final FMLCommonSetupEvent event) {}

  @Mod.EventBusSubscriber(modid = LivingChest.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
  public static class Client {
    @SubscribeEvent
    public static void initialize(final FMLClientSetupEvent event) {
      registerRenderers();
    }

    public static void registerRenderers() {
      RenderingRegistry.registerEntityRenderingHandler(EntityTypes.CHESTER.get(),
          ChesterRenderer::new);
    }
  }

  @Mod.EventBusSubscriber(modid = LivingChest.MOD_ID, bus = Bus.MOD, value = Dist.DEDICATED_SERVER)
  public static class Server {
    @SubscribeEvent
    public static void initialize(final FMLDedicatedServerSetupEvent event) {}
  }
}
