package com.syllient.livingchest.item;

import com.syllient.livingchest.animation.EyeBoneAnimation;
import com.syllient.livingchest.saveddata.ChesterSavedData;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
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

  public EyeBoneItem(final Block block) {
    super(block);
  }

  @Override
  public EnumActionResult onItemUse(final EntityPlayer player, final World worldIn,
      final BlockPos pos, final EnumHand hand, final EnumFacing facing, final float hitX,
      final float hitY, final float hitZ) {
    if (player.isSneaking()) {
      ChesterSavedData.get(worldIn).toggleChester(player, worldIn, pos);
      return EnumActionResult.SUCCESS;
    }

    return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
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
