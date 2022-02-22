package com.syllient.livingchest.client;

import com.syllient.livingchest.Proxy;
import com.syllient.livingchest.client.renderer.block.EyeBoneBlockRenderer;
import com.syllient.livingchest.client.renderer.entity.ChesterRenderer;
import com.syllient.livingchest.entity.ChesterEntity;
import com.syllient.livingchest.tile.EyeBoneTile;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy implements Proxy {
  @Override
  public void onPreInit(final FMLPreInitializationEvent event) {
    RenderingRegistry.registerEntityRenderingHandler(ChesterEntity.class, ChesterRenderer::new);

    ClientRegistry.bindTileEntitySpecialRenderer(EyeBoneTile.class, new EyeBoneBlockRenderer());
  }

  @Override
  public void onInit(final FMLInitializationEvent event) {

  }

  @Override
  public void onPostInit(final FMLPostInitializationEvent event) {

  }
}
