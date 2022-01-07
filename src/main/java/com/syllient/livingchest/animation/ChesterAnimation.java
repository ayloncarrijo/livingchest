package com.syllient.livingchest.animation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.syllient.livingchest.entity.ChesterEntity;
import com.syllient.livingchest.geckolib.controller.ExtendedAnimationController;
import com.syllient.livingchest.util.GeckoLibUtil;

import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

@SuppressWarnings("rawtypes")
public class ChesterAnimation {
  private static class Animation {
    private static final String IDLE = "animation.chester.idle";
    private static final String INIT_JUMP = "animation.chester.init_jump";
    private static final String JUMP = "animation.chester.jump";
    private static final String STOP_JUMP = "animation.chester.stop_jump";
    private static final String OPEN_MOUTH = "animation.chester.open_mouth";
    private static final String IDLE_MOUTH = "animation.chester.idle_mouth";
    private static final String CLOSE_MOUTH = "animation.chester.close_mouth";

    private static final Set<String> OPEN_MOUTH_STATES = new HashSet<String>(
        Arrays.asList(OPEN_MOUTH, IDLE_MOUTH, CLOSE_MOUTH));

    private static final Map<String, String> MOUTH_FROM_JUMP = new HashMap<String, String>();

    static {
      MOUTH_FROM_JUMP.put(Animation.INIT_JUMP, "animation.chester.mouth.init_jump");
      MOUTH_FROM_JUMP.put(Animation.JUMP, "animation.chester.mouth.jump");
      MOUTH_FROM_JUMP.put(Animation.STOP_JUMP, "animation.chester.mouth.stop_jump");
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
    final boolean isIdling = GeckoLibUtil
        .getEntityController(this.chester, Controller.JUMP)
        .getAnimationState() == AnimationState.Stopped;

    if (isIdling) {
      this.ticksIdling += 1;
    } else {
      this.ticksIdling = 0;
    }

    if (this.ticksIdling < 5) {
      return PlayState.STOP;
    }

    event.getController()
        .setAnimation(
            new AnimationBuilder()
                .addAnimation(Animation.IDLE, true));

    return PlayState.CONTINUE;
  }

  private PlayState jumpPredicate(final AnimationEvent<? extends IAnimatable> event) {
    if (GeckoLibUtil.isAnimationStopped(Animation.INIT_JUMP, event.getController())) {
      event.getController().setAnimation(
          new AnimationBuilder()
              .addAnimation(Animation.JUMP, true));

      return PlayState.CONTINUE;
    }

    final boolean isJumping = GeckoLibUtil.isCurrentAnimation(Animation.JUMP, event.getController());

    if (event.isMoving()) {
      if (!isJumping) {
        event.getController().setAnimation(
            new AnimationBuilder()
                .addAnimation(Animation.INIT_JUMP));
      }

      return PlayState.CONTINUE;
    }

    if (isJumping && ((ExtendedAnimationController) event.getController()).isAnimationFinished()) {
      event.getController().setAnimation(
          new AnimationBuilder()
              .addAnimation(Animation.STOP_JUMP));

      return PlayState.CONTINUE;
    }

    return PlayState.CONTINUE;
  }

  private PlayState mouthPredicate(final AnimationEvent<? extends IAnimatable> event) {
    if (this.chester.isMouthOpen()) {
      event.getController().setAnimation(
          new AnimationBuilder()
              .addAnimation(Animation.OPEN_MOUTH)
              .addAnimation(Animation.IDLE_MOUTH));

      return PlayState.CONTINUE;
    }

    if (GeckoLibUtil.isCurrentAnimation(Animation.IDLE_MOUTH, event.getController())) {
      event.getController().setAnimation(
          new AnimationBuilder()
              .addAnimation(Animation.CLOSE_MOUTH));

      return PlayState.CONTINUE;
    }

    if (event.getController().getAnimationState() != AnimationState.Stopped
        && Animation.OPEN_MOUTH_STATES.contains(event.getController().getCurrentAnimation().animationName)) {
      return PlayState.CONTINUE;
    }

    final AnimationController jumpController = GeckoLibUtil
        .getEntityController(this.chester, Controller.JUMP);

    if (jumpController.getAnimationState() == AnimationState.Transitioning) {
      event.getController().setAnimation(
          new AnimationBuilder().addAnimation(
              Animation.MOUTH_FROM_JUMP
                  .get(jumpController.getCurrentAnimation().animationName)));

      return PlayState.CONTINUE;
    }

    return PlayState.CONTINUE;
  }
}
