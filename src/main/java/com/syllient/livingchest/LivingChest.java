package com.syllient.livingchest;

import com.syllient.livingchest.client.renderer.block.EyeBoneBlockRenderer;
import com.syllient.livingchest.client.renderer.entity.ChesterRenderer;
import com.syllient.livingchest.eventhandler.registry.EntityRegistry;
import com.syllient.livingchest.eventhandler.registry.ItemRegistry;
import com.syllient.livingchest.eventhandler.registry.TileRegistry;
import net.minecraft.client.util.NativeUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
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
  public static void onCommonSetup(final FMLCommonSetupEvent event) {}

  @SubscribeEvent
  public static void onClientSetup(final FMLClientSetupEvent event) {
    DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> new SafeRunnable() {
      @Override
      public void run() {
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.CHESTER,
            ChesterRenderer::new);
        ClientRegistry.bindTileEntityRenderer(TileRegistry.EYE_BONE, EyeBoneBlockRenderer::new);

        event.enqueueWork(() -> {
          ItemModelsProperties.register(ItemRegistry.EYE_BONE, new ResourceLocation(MOD_ID, "idle"),
              new IItemPropertyGetter() {
                static final int ANIMATION_FRAMES = 14;
                int animationStep = 0;
                double prevSystemTime = NativeUtil.getTime();

                @Override
                public float call(final ItemStack stack, final ClientWorld world,
                    final LivingEntity entity) {
                  if (this.animationStep == ANIMATION_FRAMES + 1) {
                    this.animationStep = 0;
                  }

                  final boolean isBlinking = this.animationStep % 5 == 0;
                  final double systemTime = NativeUtil.getTime();

                  if (systemTime - this.prevSystemTime >= (isBlinking ? 0.333 : 1.500)) {
                    this.animationStep += 1;
                    this.prevSystemTime = systemTime;
                  }

                  return (float) this.animationStep / 100;
                }
              });

          ItemModelsProperties.register(ItemRegistry.EYE_BONE,
              new ResourceLocation(MOD_ID, "close"), (stack, world, entity) -> {
                return 0.0F;
              });
        });
      }
    });

  }

  @SubscribeEvent
  public static void onServerSetup(final FMLDedicatedServerSetupEvent event) {}
}
