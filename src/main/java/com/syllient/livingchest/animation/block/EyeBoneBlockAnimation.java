package com.syllient.livingchest.animation.block;

import com.syllient.livingchest.animation.Animation;
import com.syllient.livingchest.geckolib.ExtendedAnimationController;
import com.syllient.livingchest.tile.EyeBoneTile;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.CustomInstructionKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class EyeBoneBlockAnimation extends Animation<EyeBoneTile> {
  private static class Animation {
    private static final String IDLE = "animation.eye_bone.idle";
  }

  private static class Controller {
    private static final String IDLE = "idle_controller";
  }

  public EyeBoneBlockAnimation(final EyeBoneTile animatable) {
    super(animatable);
  }

  @Override
  public void registerControllers(final AnimationData data) {
    final ExtendedAnimationController<EyeBoneTile> controller =
        new ExtendedAnimationController<>(this.animatable, Controller.IDLE, 0, this::animate);

    controller.registerCustomInstructionListener(this::handleInstructionKeyframes);
    data.addAnimationController(controller);
  }

  private PlayState animate(final AnimationEvent<? extends IAnimatable> event) {
    event.getController().setAnimation(new AnimationBuilder().addAnimation(Animation.IDLE, true));
    return PlayState.CONTINUE;
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
