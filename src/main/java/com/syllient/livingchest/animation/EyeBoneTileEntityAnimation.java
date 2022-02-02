package com.syllient.livingchest.animation;

import com.syllient.livingchest.geckolib.ExtendedAnimationController;
import com.syllient.livingchest.tileentity.EyeBoneTileEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class EyeBoneTileEntityAnimation extends EyeBoneAnimation<EyeBoneTileEntity> {
  public EyeBoneTileEntityAnimation(final EyeBoneTileEntity eyeBone) {
    super(eyeBone);
  }

  @Override
  public void registerControllers(final AnimationData data) {
    data.addAnimationController(new ExtendedAnimationController<>(this.animatable, Controller.IDLE,
        0, this::idlePredicate));
  }

  private PlayState idlePredicate(final AnimationEvent<? extends IAnimatable> event) {
    event.getController().setAnimation(new AnimationBuilder().addAnimation(Animation.IDLE, true));
    return PlayState.CONTINUE;
  }
}
