package com.syllient.livingchest.tile;

import com.syllient.livingchest.animation.block.EyeBoneBlockAnimation;
import com.syllient.livingchest.eventhandler.registry.TileRegistry;
import net.minecraft.tileentity.TileEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class EyeBoneTile extends TileEntity implements IAnimatable {
  private final EyeBoneBlockAnimation animation = new EyeBoneBlockAnimation(this);
  public boolean isClosed = false;

  public EyeBoneTile() {
    super(TileRegistry.EYE_BONE);
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
