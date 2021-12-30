package com.syllient.livingchest.utils;

import net.minecraft.entity.Entity;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;

@SuppressWarnings("rawtypes")
public class AnimationUtil {
  public static boolean isAnimationRunning(String animationName, AnimationController controller) {
    return AnimationUtil.isCurrentAnimation(animationName, controller)
        && controller.getAnimationState() == AnimationState.Running;
  }

  public static boolean isAnimationTransitioning(String animationName, AnimationController controller) {
    return AnimationUtil.isCurrentAnimation(animationName, controller)
        && controller.getAnimationState() == AnimationState.Transitioning;
  }

  public static boolean isAnimationStopped(String animationName, AnimationController controller) {
    return AnimationUtil.isCurrentAnimation(animationName, controller)
        && controller.getAnimationState() == AnimationState.Stopped;
  }

  public static boolean isCurrentAnimation(String animationName, AnimationController controller) {
    return controller.getCurrentAnimation() != null
        && controller.getCurrentAnimation().animationName.equals(animationName);
  }

  public static boolean hasReachedKeyframe(String keyFrameName, AnimationController controller) {
    if (controller.getCurrentAnimation() == null) {
      return false;
    }

    return controller.getCurrentAnimation().soundKeyFrames
        .stream()
        .filter((keyFrame) -> keyFrame.getEventData().equals(keyFrameName))
        .findAny()
        .get().hasExecuted;
  }

  public static <T extends Entity & IAnimatable> AnimationController getEntityController(
      T entity,
      String controllerName) {
    return entity
        .getFactory()
        .getOrCreateAnimationData(entity.getUniqueID().hashCode())
        .getAnimationControllers()
        .get(controllerName);
  }
}
