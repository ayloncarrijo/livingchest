package com.syllient.livingchest.client;

import com.syllient.livingchest.IProxy;
import com.syllient.livingchest.client.renderer.entity.RenderChester;
import com.syllient.livingchest.entity.EntityChester;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy implements IProxy {
  @Override
  public void onPreInit(FMLPreInitializationEvent event) {
    RenderingRegistry.registerEntityRenderingHandler(
        EntityChester.class, RenderChester::new);
  }

  @Override
  public void onInit(FMLInitializationEvent event) {

  }

  @Override
  public void onPostInit(FMLPostInitializationEvent event) {

  }
}
