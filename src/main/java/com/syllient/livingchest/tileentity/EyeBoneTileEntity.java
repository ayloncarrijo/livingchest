package com.syllient.livingchest.tileentity;

import net.minecraft.tileentity.TileEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class EyeBoneTileEntity extends TileEntity implements IAnimatable {
  private final AnimationFactory factory = new AnimationFactory(this);

  @Override
  public void registerControllers(final AnimationData data) {

  }

  @Override
  public AnimationFactory getFactory() {
    return this.factory;
  }
}
