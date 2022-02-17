package com.syllient.livingchest;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import software.bernie.geckolib3.GeckoLib;

@Mod("livingchest")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class LivingChest {
  public LivingChest() {
    GeckoLib.initialize();
  }

  @SubscribeEvent
  public static void onCommonSetup(final FMLCommonSetupEvent event) {}

  @SubscribeEvent
  public static void onClientSetup(final FMLClientSetupEvent event) {}

  @SubscribeEvent
  public static void onServerSetup(final FMLDedicatedServerSetupEvent event) {}
}
