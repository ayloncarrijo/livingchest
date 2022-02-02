package com.syllient.livingchest.tileentity;

import com.syllient.livingchest.animation.EyeBoneTileEntityAnimation;
import net.minecraft.tileentity.TileEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class EyeBoneTileEntity extends TileEntity implements IAnimatable {
  private final EyeBoneTileEntityAnimation animation = new EyeBoneTileEntityAnimation(this);

  @Override
  public void registerControllers(final AnimationData data) {
    this.animation.registerControllers(data);
  }

  @Override
  public AnimationFactory getFactory() {
    return this.animation.getFactory();
  }
}
