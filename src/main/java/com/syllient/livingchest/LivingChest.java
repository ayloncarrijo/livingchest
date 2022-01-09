package com.syllient.livingchest;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import software.bernie.geckolib3.GeckoLib;

@Mod(modid = LivingChest.MOD_ID, name = LivingChest.NAME, version = LivingChest.VERSION)
public class LivingChest implements IProxy {
  public static final String MOD_ID = "livingchest";
  public static final String NAME = "LivingChest";
  public static final String VERSION = "1.0";

  @Mod.Instance(LivingChest.MOD_ID)
  public static LivingChest instance;

  @SidedProxy(clientSide = "com.syllient.livingchest.client.ClientProxy", serverSide = "com.syllient.livingchest.server.ServerProxy")
  public static IProxy proxy;

  public LivingChest() {
    GeckoLib.initialize();
  }

  @Override
  @Mod.EventHandler
  public void onPreInit(final FMLPreInitializationEvent event) {
    NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    LivingChest.proxy.onPreInit(event);
  }

  @Override
  @Mod.EventHandler
  public void onInit(final FMLInitializationEvent event) {
    LivingChest.proxy.onInit(event);
  }

  @Override
  @Mod.EventHandler
  public void onPostInit(final FMLPostInitializationEvent event) {
    LivingChest.proxy.onPostInit(event);
  }
}
