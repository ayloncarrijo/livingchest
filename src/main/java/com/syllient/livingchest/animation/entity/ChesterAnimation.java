package com.syllient.livingchest.animation.entity;

import com.syllient.livingchest.animation.Animation;
import com.syllient.livingchest.entity.ChesterEntity;
import software.bernie.geckolib3.core.manager.AnimationData;

public class ChesterAnimation extends Animation<ChesterEntity> {
  public ChesterAnimation(final ChesterEntity animatable) {
    super(animatable);
  }

  @Override
  public void registerControllers(final AnimationData data) {}
}
