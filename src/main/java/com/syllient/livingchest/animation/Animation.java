package com.syllient.livingchest.animation;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class Animation<T extends IAnimatable> {
  protected final T animatable;
  protected final AnimationFactory factory;

  public Animation(final T animatable) {
    this.animatable = animatable;
    this.factory = new AnimationFactory(animatable);
  }

  public void registerControllers(final AnimationData data) {}

  public AnimationFactory getFactory() {
    return this.factory;
  }
}
