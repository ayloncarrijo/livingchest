package com.syllient.livingchest.animation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.syllient.livingchest.entity.ChesterEntity;
import com.syllient.livingchest.geckolib.controller.ExtendedAnimationController;
import com.syllient.livingchest.util.GeckoLibUtil;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

@SuppressWarnings("unchecked")
public class ChesterAnimation {
  private static class Animation {
    private static final String IDLE = "animation.chester.idle";
    private static final String INIT_JUMP = "animation.chester.init_jump";
    private static final String JUMP = "animation.chester.jump";
    private static final String STOP_JUMP = "animation.chester.stop_jump";
    private static final String OPEN_MOUTH = "animation.chester.open_mouth";
    private static final String IDLE_MOUTH = "animation.chester.idle_mouth";
    private static final String CLOSE_MOUTH = "animation.chester.close_mouth";

    private static final Map<String, String> MOUTH_FROM_JUMP = new HashMap<>();
    private static final Set<String> MOUTH_STATES = new HashSet<>(
        Arrays.asList(OPEN_MOUTH, IDLE_MOUTH, CLOSE_MOUTH));

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

  private final ChesterEntity chester;
  private int ticksIdling = 0;

  public ChesterAnimation(final ChesterEntity chester) {
    this.chester = chester;
  }

  public void registerControllers(final AnimationData data) {
    final AnimationController<ChesterEntity> idleController = new ExtendedAnimationController<ChesterEntity>(
        this.chester,
        Controller.IDLE, 0, this::idlePredicate);

    final AnimationController<ChesterEntity> jumpController = new ExtendedAnimationController<ChesterEntity>(
        this.chester,
        Controller.JUMP, 0, this::jumpPredicate);

    final AnimationController<ChesterEntity> mouthController = new ExtendedAnimationController<ChesterEntity>(
        this.chester,
        Controller.MOUTH, 0, this::mouthPredicate);

    data.addAnimationController(idleController);
    data.addAnimationController(jumpController);
    data.addAnimationController(mouthController);
  }

  private PlayState idlePredicate(final AnimationEvent<? extends IAnimatable> event) {
    final ExtendedAnimationController<ChesterEntity> idleController = (ExtendedAnimationController<ChesterEntity>) event
        .getController();

    final ExtendedAnimationController<ChesterEntity> jumpController = (ExtendedAnimationController<ChesterEntity>) GeckoLibUtil
        .getEntityController(this.chester, Controller.JUMP);

    final boolean isIdling = jumpController.isAnimationStopped();

    if (isIdling) {
      this.ticksIdling += 1;
    } else {
      this.ticksIdling = 0;
    }

    if (this.ticksIdling < 5) {
      return PlayState.STOP;
    }

    idleController.setAnimation(
        new AnimationBuilder().addAnimation(Animation.IDLE, true));

    return PlayState.CONTINUE;
  }

  private PlayState jumpPredicate(final AnimationEvent<? extends IAnimatable> event) {
    final ExtendedAnimationController<ChesterEntity> jumpController = (ExtendedAnimationController<ChesterEntity>) event
        .getController();

    if (jumpController.isAnimationStopped(Animation.INIT_JUMP)) {
      jumpController.setAnimation(
          new AnimationBuilder().addAnimation(Animation.JUMP));

      return PlayState.CONTINUE;
    }

    final boolean isJumping = jumpController.isCurrentAnimation(Animation.JUMP);

    if (event.isMoving()) {
      if (!isJumping) {
        jumpController.setAnimation(
            new AnimationBuilder()
                .addAnimation(Animation.INIT_JUMP));
      }

      return PlayState.CONTINUE;
    }

    if (isJumping && jumpController.isAnimationFinished()) {
      jumpController.setAnimation(
          new AnimationBuilder()
              .addAnimation(Animation.STOP_JUMP));

      return PlayState.CONTINUE;
    }

    return PlayState.CONTINUE;
  }

  private PlayState mouthPredicate(final AnimationEvent<? extends IAnimatable> event) {
    final ExtendedAnimationController<ChesterEntity> mouthController = (ExtendedAnimationController<ChesterEntity>) event
        .getController();

    if (this.chester.isMouthOpen()) {
      mouthController.setAnimation(
          new AnimationBuilder()
              .addAnimation(Animation.OPEN_MOUTH)
              .addAnimation(Animation.IDLE_MOUTH));

      return PlayState.CONTINUE;
    }

    if (mouthController.isCurrentAnimation(Animation.IDLE_MOUTH)) {
      mouthController.setAnimation(
          new AnimationBuilder()
              .addAnimation(Animation.CLOSE_MOUTH));

      return PlayState.CONTINUE;
    }

    if (mouthController.isCurrentAnimation(Animation.MOUTH_STATES) && !mouthController.isAnimationStopped()) {
      return PlayState.CONTINUE;
    }

    final ExtendedAnimationController<ChesterEntity> jumpController = (ExtendedAnimationController<ChesterEntity>) GeckoLibUtil
        .getEntityController(this.chester, Controller.JUMP);

    if (jumpController.isAnimationTransitioning()) {
      mouthController.setAnimation(
          new AnimationBuilder()
              .addAnimation(Animation.MOUTH_FROM_JUMP
                  .get(jumpController.getCurrentAnimation().animationName)));

      return PlayState.CONTINUE;
    }

    return PlayState.CONTINUE;
  }
}
