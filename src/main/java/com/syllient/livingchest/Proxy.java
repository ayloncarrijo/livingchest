package com.syllient.livingchest;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface Proxy {
  public void onPreInit(FMLPreInitializationEvent event);

  public void onInit(FMLInitializationEvent event);

  public void onPostInit(FMLPostInitializationEvent event);
}
