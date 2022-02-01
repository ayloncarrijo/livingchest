package com.syllient.livingchest;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import software.bernie.geckolib3.GeckoLib;

@Mod(modid = LivingChest.MOD_ID, name = LivingChest.NAME, version = LivingChest.VERSION)
public class LivingChest implements Proxy {
  public static final String MOD_ID = "livingchest";
  public static final String NAME = "LivingChest";
  public static final String VERSION = "1.0";

  @Mod.Instance(LivingChest.MOD_ID)
  public static LivingChest INSTANCE;

  @SidedProxy(clientSide = "com.syllient.livingchest.client.ClientProxy",
      serverSide = "com.syllient.livingchest.server.ServerProxy")
  public static Proxy PROXY;

  public LivingChest() {
    GeckoLib.initialize();
  }

  @Mod.EventHandler
  @Override
  public void onPreInit(final FMLPreInitializationEvent event) {
    NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    PROXY.onPreInit(event);
  }

  @Mod.EventHandler
  @Override
  public void onInit(final FMLInitializationEvent event) {
    PROXY.onInit(event);
  }

  @Mod.EventHandler
  @Override
  public void onPostInit(final FMLPostInitializationEvent event) {
    PROXY.onPostInit(event);
  }
}
