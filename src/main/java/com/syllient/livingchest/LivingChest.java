package com.syllient.livingchest;

import com.syllient.livingchest.client.renderer.block.EyeBoneBlockRenderer;
import com.syllient.livingchest.client.renderer.entity.ChesterRenderer;
import com.syllient.livingchest.entity.ChesterEntity;
import com.syllient.livingchest.tile.EyeBoneTile;
import com.syllient.livingchest.util.DistExecutor;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import software.bernie.geckolib3.GeckoLib;

@Mod(modid = LivingChest.MOD_ID, name = LivingChest.NAME, version = LivingChest.VERSION,
    dependencies = LivingChest.DEPENDENCIES)
public class LivingChest {
  public static final String MOD_ID = "livingchest";
  public static final String NAME = "LivingChest";
  public static final String VERSION = "1.0.0.1";
  public static final String DEPENDENCIES = "required-after:geckolib3@[3.0.20,);";

  @Mod.Instance(LivingChest.MOD_ID)
  public static LivingChest INSTANCE;

  public LivingChest() {
    GeckoLib.initialize();
  }

  @Mod.EventHandler
  public void onPreInit(final FMLPreInitializationEvent event) {
    NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    PacketHandler.initialize();

    DistExecutor.runWhenOn(Side.CLIENT, () -> new Runnable() {
      @Override
      public void run() {
        RenderingRegistry.registerEntityRenderingHandler(ChesterEntity.class, ChesterRenderer::new);

        ClientRegistry.bindTileEntitySpecialRenderer(EyeBoneTile.class, new EyeBoneBlockRenderer());
      }
    });
  }

  @Mod.EventHandler
  public void onInit(final FMLInitializationEvent event) {}

  @Mod.EventHandler
  public void onPostInit(final FMLPostInitializationEvent event) {}
}
