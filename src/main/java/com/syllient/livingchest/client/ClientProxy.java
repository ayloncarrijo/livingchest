package com.syllient.livingchest.client;

import com.syllient.livingchest.IProxy;
import com.syllient.livingchest.client.renderer.ChesterRenderer;
import com.syllient.livingchest.client.renderer.EyeBoneTileEntityRenderer;
import com.syllient.livingchest.entity.ChesterEntity;
import com.syllient.livingchest.tileentity.EyeBoneTileEntity;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy implements IProxy {
  @Override
  public void onPreInit(final FMLPreInitializationEvent event) {
    RenderingRegistry.registerEntityRenderingHandler(
        ChesterEntity.class, ChesterRenderer::new);

    ClientRegistry.bindTileEntitySpecialRenderer(
        EyeBoneTileEntity.class, new EyeBoneTileEntityRenderer());
  }

  @Override
  public void onInit(final FMLInitializationEvent event) {

  }

  @Override
  public void onPostInit(final FMLPostInitializationEvent event) {

  }
}
