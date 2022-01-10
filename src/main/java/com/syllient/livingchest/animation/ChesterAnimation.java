package com.syllient.livingchest.animation;

import com.syllient.livingchest.entity.ChesterEntity;
import com.syllient.livingchest.geckolib.controller.ExtendedAnimationController;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class ChesterAnimation {
  private static class Animation {
    private static final String IDLE = "animation.chester.idle";
    private static final String INIT_JUMP = "animation.chester.init_jump";
    private static final String JUMP = "animation.chester.jump";
    private static final String STOP_JUMP = "animation.chester.stop_jump";
    private static final String OPEN_MOUTH = "animation.chester.open_mouth";
    private static final String IDLE_MOUTH = "animation.chester.idle_mouth";
    private static final String CLOSE_MOUTH = "animation.chester.close_mouth";
  }

  private static class Controller {
    private static final String IDLE = "idle_controller";
    private static final String JUMP = "jump_controller";
    private static final String MOUTH = "mouth_controller";
  }

  private final ExtendedAnimationController<ChesterEntity> idleController;
  private final ExtendedAnimationController<ChesterEntity> jumpController;
  private final ExtendedAnimationController<ChesterEntity> openController;
  private final ChesterEntity chester;
  private int ticksIdling = 0;

  public ChesterAnimation(final ChesterEntity chester) {
    this.idleController = new ExtendedAnimationController<>(
        chester,
        Controller.IDLE, 0, this::idlePredicate);
    this.jumpController = new ExtendedAnimationController<>(
        chester,
        Controller.JUMP, 0, this::jumpPredicate);
    this.openController = new ExtendedAnimationController<>(
        chester,
        Controller.MOUTH, 0, this::openPredicate);
    this.chester = chester;
  }

  public void registerControllers(final AnimationData data) {
    data.addAnimationController(this.idleController);
    data.addAnimationController(this.jumpController);
    data.addAnimationController(this.openController);
  }

  private PlayState idlePredicate(final AnimationEvent<? extends IAnimatable> event) {
    final boolean isIdling = this.jumpController.isAnimationStopped();

    if (isIdling) {
      this.ticksIdling += 1;
    } else {
      this.ticksIdling = 0;
    }

    if (this.ticksIdling < 5) {
      return PlayState.STOP;
    }

    this.idleController.setAnimation(
        new AnimationBuilder().addAnimation(Animation.IDLE, true));

    return PlayState.CONTINUE;
  }

  private PlayState jumpPredicate(final AnimationEvent<? extends IAnimatable> event) {
    if (this.jumpController.isAnimationStopped(Animation.INIT_JUMP)) {
      this.jumpController.setAnimation(
          new AnimationBuilder().addAnimation(Animation.JUMP));

      return PlayState.CONTINUE;
    }

    final boolean isJumping = this.jumpController.isCurrentAnimation(Animation.JUMP);

    if (event.isMoving() && this.chester.onGround && !this.chester.isMouthOpen()) {
      if (!isJumping) {
        this.jumpController.setAnimation(
            new AnimationBuilder()
                .addAnimation(Animation.INIT_JUMP));
      }

      return PlayState.CONTINUE;
    }

    if (isJumping && this.jumpController.isAnimationJustFinished()) {
      this.jumpController.setAnimation(
          new AnimationBuilder()
              .addAnimation(Animation.STOP_JUMP));

      return PlayState.CONTINUE;
    }

    return PlayState.CONTINUE;
  }

  private PlayState openPredicate(final AnimationEvent<? extends IAnimatable> event) {
    if (this.ticksIdling < 5) {
      return PlayState.STOP;
    }

    if (this.chester.isMouthOpen()) {
      this.openController.setAnimation(
          new AnimationBuilder()
              .addAnimation(Animation.OPEN_MOUTH)
              .addAnimation(Animation.IDLE_MOUTH));

      return PlayState.CONTINUE;
    }

    if (this.openController.isCurrentAnimation(Animation.IDLE_MOUTH)) {
      this.openController.setAnimation(
          new AnimationBuilder()
              .addAnimation(Animation.CLOSE_MOUTH));

      return PlayState.CONTINUE;
    }

    return PlayState.CONTINUE;
  }
}
