package com.syllient.livingchest.item;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.eventhandler.registry.BlockRegistry;
import com.syllient.livingchest.eventhandler.registry.ItemRegistry;
import com.syllient.livingchest.saveddata.VirtualChesterSavedData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.NativeUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;

public class EyeBoneItem extends BlockItem {
  public EyeBoneItem() {
    super(BlockRegistry.EYE_BONE, new Item.Properties().tab(ItemGroup.TAB_MISC).stacksTo(1));
  }

  @Override
  public ActionResultType useOn(final ItemUseContext ctx) {
    final World world = ctx.getLevel();
    final PlayerEntity player = ctx.getPlayer();
    final BlockPos pos = ctx.getClickedPos();

    if (player.isCrouching()) {
      if (!world.isClientSide) {
        VirtualChesterSavedData.getServerInstance(world).toggleChester(player, pos);
      }

      return ActionResultType.SUCCESS;
    }

    return super.useOn(ctx);
  }

  public static void createProperties() {
    DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> new SafeRunnable() {
      @Override
      public void run() {
        ItemModelsProperties.register(ItemRegistry.EYE_BONE,
            new ResourceLocation(LivingChest.MOD_ID, "idle"), new IItemPropertyGetter() {
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
            new ResourceLocation(LivingChest.MOD_ID, "close"), (stack, world, entity) -> {
              return entity instanceof PlayerEntity
                  && VirtualChesterSavedData.getClientInstance(Minecraft.getInstance().level)
                      .getVirtualChester(entity.getUUID()).isDead() ? 1.0F : 0.0F;
            });
      }
    });
  }
}
