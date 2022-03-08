package com.syllient.livingchest;

import com.syllient.livingchest.client.gui.ChesterGui;
import com.syllient.livingchest.client.renderer.block.EyeBoneBlockRenderer;
import com.syllient.livingchest.client.renderer.entity.ChesterRenderer;
import com.syllient.livingchest.eventhandler.registry.ContainerRegistry;
import com.syllient.livingchest.eventhandler.registry.EntityRegistry;
import com.syllient.livingchest.eventhandler.registry.TileRegistry;
import com.syllient.livingchest.item.EyeBoneItem;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import software.bernie.geckolib3.GeckoLib;

@Mod(LivingChest.MOD_ID)
@Mod.EventBusSubscriber(modid = LivingChest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LivingChest {
  public static final String MOD_ID = "livingchest";

  public LivingChest() {
    GeckoLib.initialize();
  }

  @SubscribeEvent
  public static void onCommonSetup(final FMLCommonSetupEvent event) {
    PacketHandler.initialize();
  }

  @SubscribeEvent
  public static void onClientSetup(final FMLClientSetupEvent event) {
    DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> new SafeRunnable() {
      @Override
      public void run() {
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.CHESTER,
            ChesterRenderer::new);
        ClientRegistry.bindTileEntityRenderer(TileRegistry.EYE_BONE, EyeBoneBlockRenderer::new);
        ScreenManager.register(ContainerRegistry.CHESTER, ChesterGui::new);

        event.enqueueWork(() -> {
          EyeBoneItem.createProperties();
        });
      }
    });

  }

  @SubscribeEvent
  public static void onServerSetup(final FMLDedicatedServerSetupEvent event) {}
}
