package com.syllient.livingchest.item;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.eventhandler.registry.BlockRegistry;
import com.syllient.livingchest.saveddata.VirtualChesterSavedData;
import com.syllient.livingchest.util.DistExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

public class EyeBoneItem extends ItemBlock {
  public EyeBoneItem() {
    super(BlockRegistry.EYE_BONE);
    this.setMaxStackSize(1);
    this.createProperties();
  }

  @Override
  public EnumActionResult onItemUse(final EntityPlayer player, final World world,
      final BlockPos pos, final EnumHand hand, final EnumFacing facing, final float hitX,
      final float hitY, final float hitZ) {
    if (player.isSneaking()) {
      if (!world.isRemote) {
        VirtualChesterSavedData.getServerInstance(world).toggleChester(player, pos);
      }

      return EnumActionResult.SUCCESS;
    }

    return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
  }

  private void createProperties() {
    DistExecutor.runWhenOn(Side.CLIENT, () -> new Runnable() {
      @Override
      public void run() {
        EyeBoneItem.this.addPropertyOverride(new ResourceLocation(LivingChest.MOD_ID, "idle"),
            new IItemPropertyGetter() {
              static final int ANIMATION_FRAMES = 14;
              int animationStep = 0;
              long prevSystemTime = Minecraft.getSystemTime();

              @Override
              public float apply(final ItemStack stack, final World worldIn,
                  final EntityLivingBase entityIn) {
                if (this.animationStep == ANIMATION_FRAMES + 1) {
                  this.animationStep = 0;
                }

                final boolean isBlinking = this.animationStep % 5 == 0;
                final long systemTime = Minecraft.getSystemTime();

                if (systemTime - this.prevSystemTime >= (isBlinking ? 333 : 1500)) {
                  this.animationStep += 1;
                  this.prevSystemTime = systemTime;
                }

                return (float) this.animationStep / 100;
              }
            });

        EyeBoneItem.this.addPropertyOverride(new ResourceLocation(LivingChest.MOD_ID, "close"),
            (stack, world, entity) -> {
              return entity instanceof EntityPlayer
                  && VirtualChesterSavedData.getClientInstance(Minecraft.getMinecraft().world)
                      .getVirtualChester(entity.getUniqueID()).isDead() ? 1.0F : 0.0F;
            });
      }
    });
  }
}
