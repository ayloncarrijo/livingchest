package com.syllient.livingchest.item;

import com.syllient.livingchest.animation.EyeBoneAnimation;
import com.syllient.livingchest.entity.ChesterEntity;
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
      if (!worldIn.isRemote) {
        this.spawnChester(player, worldIn, pos);
      }

      return EnumActionResult.SUCCESS;
    }

    return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
  }

  public void spawnChester(final EntityPlayer player, final World worldIn, final BlockPos pos) {
    // if (ChesterSavedData.get(worldIn).isChesterDead(player)) {
    // System.out.println("chester deste player morto");
    // return;
    // }

    final ChesterEntity chester = new ChesterEntity(worldIn);
    chester.setTamedBy(player);
    chester.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0.0F, 0.0F);
    worldIn.spawnEntity(chester);
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
