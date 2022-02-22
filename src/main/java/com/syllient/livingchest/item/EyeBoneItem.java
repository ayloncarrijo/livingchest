package com.syllient.livingchest.item;

import com.syllient.livingchest.animation.block.EyeBoneAnimation;
import com.syllient.livingchest.registry.BlockRegistry;
import com.syllient.livingchest.saveddata.VirtualChesterSavedData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class EyeBoneItem extends ItemBlock implements IAnimatable {
  private final EyeBoneAnimation<EyeBoneItem> animation = new EyeBoneAnimation<>(this);

  public EyeBoneItem() {
    super(BlockRegistry.EYE_BONE);
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

  @Override
  public int getItemStackLimit(final ItemStack stack) {
    return 1;
  }

  @Override
  public void registerControllers(final AnimationData data) {
    this.animation.registerControllers(data);
  }

  @Override
  public AnimationFactory getFactory() {
    return this.animation.getFactory();
  }
}
