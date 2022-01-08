package com.syllient.livingchest.animation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    private static final Set<String> ALL_MOUTH_STEPS = new HashSet<>(
        Arrays.asList(
            Animation.OPEN_MOUTH,
            Animation.IDLE_MOUTH,
            Animation.CLOSE_MOUTH));

    private static final Map<String, String> MOUTH_FROM_JUMP = new HashMap<>();

    static {
      Animation.MOUTH_FROM_JUMP.put(Animation.INIT_JUMP, "animation.chester.mouth.init_jump");
      Animation.MOUTH_FROM_JUMP.put(Animation.JUMP, "animation.chester.mouth.jump");
      Animation.MOUTH_FROM_JUMP.put(Animation.STOP_JUMP, "animation.chester.mouth.stop_jump");
    }
  }

  private static class Controller {
    private static final String IDLE = "idle_controller";
    private static final String JUMP = "jump_controller";
    private static final String MOUTH = "mouth_controller";
  }

  private final ExtendedAnimationController<ChesterEntity> idleController;
  private final ExtendedAnimationController<ChesterEntity> jumpController;
  private final ExtendedAnimationController<ChesterEntity> mouthController;
  private final ChesterEntity chester;
  private int ticksIdling = 0;

  public ChesterAnimation(final ChesterEntity chester) {
    this.chester = chester;

    this.idleController = new ExtendedAnimationController<>(
        this.chester,
        Controller.IDLE, 0, this::idlePredicate);

    this.jumpController = new ExtendedAnimationController<>(
        this.chester,
        Controller.JUMP, 0, this::jumpPredicate);

    this.mouthController = new ExtendedAnimationController<>(
        this.chester,
        Controller.MOUTH, 0, this::mouthPredicate);
  }

  public void registerControllers(final AnimationData data) {
    data.addAnimationController(this.idleController);
    data.addAnimationController(this.jumpController);
    data.addAnimationController(this.mouthController);
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

    if (event.isMoving()) {
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

  private PlayState mouthPredicate(final AnimationEvent<? extends IAnimatable> event) {
    if (this.chester.isMouthOpen()) {
      this.mouthController.setAnimation(
          new AnimationBuilder()
              .addAnimation(Animation.OPEN_MOUTH)
              .addAnimation(Animation.IDLE_MOUTH));

      return PlayState.CONTINUE;
    }

    if (this.mouthController.isCurrentAnimation(Animation.IDLE_MOUTH)) {
      this.mouthController.setAnimation(
          new AnimationBuilder()
              .addAnimation(Animation.CLOSE_MOUTH));

      return PlayState.CONTINUE;
    }

    if (this.mouthController.isCurrentAnimation(Animation.ALL_MOUTH_STEPS)
        && !this.mouthController.isAnimationStopped()) {
      return PlayState.CONTINUE;
    }

    if (this.jumpController.isAnimationTransitioning()) {
      this.mouthController.setAnimation(
          new AnimationBuilder()
              .addAnimation(
                  Animation.MOUTH_FROM_JUMP.get(
                      this.jumpController.getCurrentAnimation().animationName)));

      return PlayState.CONTINUE;
    }

    return PlayState.CONTINUE;
  }
}
