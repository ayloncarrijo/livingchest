package com.syllient.livingchest.item;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.eventhandler.registry.BlockRegistry;
import com.syllient.livingchest.saveddata.VirtualChesterSavedData;
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

public class EyeBoneItem extends ItemBlock {
  public EyeBoneItem() {
    super(BlockRegistry.EYE_BONE);
    this.setMaxStackSize(1);
    this.addPropertyOverride(new ResourceLocation(LivingChest.MOD_ID, "idle"),
        new IItemPropertyGetter() {
          int animationStep = 0;
          long lastWorldTime;

          @Override
          public float apply(final ItemStack stack, final World worldIn,
              final EntityLivingBase entity) {
            if (worldIn == null && entity == null) {
              return 0.0F;
            }

            final World world = worldIn != null ? worldIn : entity.world;

            if (this.lastWorldTime != world.getTotalWorldTime()
                && world.getTotalWorldTime() % 40 == 0) {
              this.animationStep += 1;
              this.lastWorldTime = world.getTotalWorldTime();
            }

            if (this.animationStep > 8) {
              this.animationStep = 0;
            }

            System.out.println((float) this.animationStep / 100);

            return (float) this.animationStep / 100;
          }
        });
    this.addPropertyOverride(new ResourceLocation(LivingChest.MOD_ID, "close"),
        (final ItemStack stack, final World world, final EntityLivingBase entity) -> {
          return entity instanceof EntityPlayer && VirtualChesterSavedData.getInstance(entity.world)
              .getVirtualChester(entity.getUniqueID()).isDead() ? 1.0F : 0.0F;
        });

  }

  @Override
  public EnumActionResult onItemUse(final EntityPlayer player, final World world,
      final BlockPos pos, final EnumHand hand, final EnumFacing facing, final float hitX,
      final float hitY, final float hitZ) {
    if (player.isSneaking()) {
      VirtualChesterSavedData.getInstance(world).toggleChester(player, world, pos);
      return EnumActionResult.SUCCESS;
    }

    return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
  }
}
