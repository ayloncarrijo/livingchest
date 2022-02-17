package com.syllient.livingchest.tile;

import com.syllient.livingchest.registry.TileRegistry;
import net.minecraft.tileentity.TileEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class EyeBoneTile extends TileEntity implements IAnimatable {
  public EyeBoneTile() {
    super(TileRegistry.EYE_BONE);
  }

  @Override
  public void registerControllers(final AnimationData data) {}

  @Override
  public AnimationFactory getFactory() {
    return null;
  }
}
