package com.syllient.livingchest.animation.animator.block;

import com.syllient.livingchest.animation.animator.Animator;
import com.syllient.livingchest.animation.controller.OrderedAnimationController;
import com.syllient.livingchest.tile.EyeBoneTile;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.CustomInstructionKeyframeEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class EyeBoneBlockAnimator extends Animator<EyeBoneTile> {
  private static class Animation {
    private static final String IDLE = "animation.eye_bone.idle";
  }

  private static class Controller {
    private static final String IDLE = "controller.idle";
  }

  public EyeBoneBlockAnimator(final EyeBoneTile animatable) {
    super(animatable);
  }

  @Override
  public void registerControllers(final AnimationData data) {
    final OrderedAnimationController<EyeBoneTile> idleController =
        new OrderedAnimationController<>(this, Controller.IDLE, (controller) -> true,
            (event) -> new AnimationBuilder().addAnimation(Animation.IDLE, true));

    data.addAnimationController(idleController);

    idleController.registerCustomInstructionListener(this::handleInstructionKeyframes);
  }

  private void handleInstructionKeyframes(
      final CustomInstructionKeyframeEvent<? extends IAnimatable> event) {
    switch (event.instructions) {
      case "open": {
        this.animatable.isClosed = false;
        break;
      }
      case "close": {
        this.animatable.isClosed = true;
        break;
      }
      default: {
        return;
      }
    }
  }
}
