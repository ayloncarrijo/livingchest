package com.syllient.livingchest;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import software.bernie.geckolib3.GeckoLib;

@Mod(modid = LivingChest.MODID, name = LivingChest.NAME, version = LivingChest.VERSION)
public class LivingChest implements IProxy {
  public static final String MODID = "livingchest";
  public static final String NAME = "LivingChest";
  public static final String VERSION = "1.0";

  @SidedProxy(clientSide = "com.syllient.livingchest.client.ClientProxy", serverSide = "com.syllient.livingchest.server.ServerProxy")
  public static IProxy proxy;

  public LivingChest() {
    GeckoLib.initialize();
  }

  @Override
  @EventHandler
  public void onPreInit(FMLPreInitializationEvent event) {
    LivingChest.proxy.onPreInit(event);
  }

  @Override
  @EventHandler
  public void onInit(FMLInitializationEvent event) {
    LivingChest.proxy.onInit(event);
  }

  @Override
  @EventHandler
  public void onPostInit(FMLPostInitializationEvent event) {
    LivingChest.proxy.onPostInit(event);
  }
}
