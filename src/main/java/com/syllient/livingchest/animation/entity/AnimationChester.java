package com.syllient.livingchest.animation.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.syllient.livingchest.entity.EntityChester;
import com.syllient.livingchest.gecko.AnimationControllerExtended;
import com.syllient.livingchest.util.AnimationUtils;

import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

@SuppressWarnings("rawtypes")
public class AnimationChester {
  private static class Animation {
    private static final String IDLE = "animation.chester.idle";
    private static final String INIT_JUMP = "animation.chester.init_jump";
    private static final String JUMP = "animation.chester.jump";
    private static final String STOP_JUMP = "animation.chester.stop_jump";
    private static final String OPEN_MOUTH = "animation.chester.open_mouth";
    private static final String IDLE_MOUTH = "animation.chester.idle_mouth";
    private static final String CLOSE_MOUTH = "animation.chester.close_mouth";

    private static final Map<String, String> ANIM_JUMP_TO_MOUTH = new HashMap<String, String>();

    private static final Set<String> OPEN_MOUTH_STATES = new HashSet<String>(
        Arrays.asList(OPEN_MOUTH, IDLE_MOUTH, CLOSE_MOUTH));

    static {
      ANIM_JUMP_TO_MOUTH.put(Animation.INIT_JUMP, "animation.chester.mouth.init_jump");
      ANIM_JUMP_TO_MOUTH.put(Animation.JUMP, "animation.chester.mouth.jump");
      ANIM_JUMP_TO_MOUTH.put(Animation.STOP_JUMP, "animation.chester.mouth.stop_jump");
    }
  }

  private static class Controller {
    private static final String IDLE = "idle_controller";
    private static final String JUMP = "jump_controller";
    private static final String MOUTH = "mouth_controller";
  }

  private final EntityChester chester;
  private int ticksIdling = 0;

  public AnimationChester(final EntityChester chester) {
    this.chester = chester;
  }

  public void registerControllers(final AnimationData data) {
    final AnimationController<EntityChester> idleController = new AnimationController<EntityChester>(this.chester,
        Controller.IDLE, 0, this::idlePredicate);

    final AnimationController<EntityChester> jumpController = new AnimationControllerExtended<EntityChester>(
        this.chester,
        Controller.JUMP, 0, this::jumpPredicate);

    final AnimationController<EntityChester> mouthController = new AnimationController<EntityChester>(this.chester,
        Controller.MOUTH, 0, this::mouthPredicate);

    data.addAnimationController(idleController);
    data.addAnimationController(jumpController);
    data.addAnimationController(mouthController);
  }

  private PlayState idlePredicate(final AnimationEvent<? extends IAnimatable> event) {
    final boolean isIdling = AnimationUtils
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
    if (AnimationUtils.isAnimationStopped(Animation.INIT_JUMP, event.getController())) {
      event.getController().setAnimation(
          new AnimationBuilder()
              .addAnimation(Animation.JUMP, true));

      return PlayState.CONTINUE;
    }

    final boolean isJumping = AnimationUtils.isCurrentAnimation(Animation.JUMP, event.getController());

    if (event.isMoving()) {
      if (!isJumping) {
        event.getController().setAnimation(
            new AnimationBuilder()
                .addAnimation(Animation.INIT_JUMP));
      }

      return PlayState.CONTINUE;
    }

    if (isJumping && ((AnimationControllerExtended) event.getController()).isAnimationFinished()) {
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

    if (AnimationUtils.isCurrentAnimation(Animation.IDLE_MOUTH, event.getController())) {
      event.getController().setAnimation(
          new AnimationBuilder()
              .addAnimation(Animation.CLOSE_MOUTH));

      return PlayState.CONTINUE;
    }

    if (event.getController().getAnimationState() != AnimationState.Stopped
        && Animation.OPEN_MOUTH_STATES.contains(event.getController().getCurrentAnimation().animationName)) {
      return PlayState.CONTINUE;
    }

    final AnimationController jumpController = AnimationUtils
        .getEntityController(this.chester, Controller.JUMP);

    if (jumpController.getAnimationState() == AnimationState.Transitioning) {
      event.getController().setAnimation(
          new AnimationBuilder().addAnimation(
              Animation.ANIM_JUMP_TO_MOUTH
                  .get(jumpController.getCurrentAnimation().animationName)));

      return PlayState.CONTINUE;
    }

    return PlayState.CONTINUE;
  }
}
