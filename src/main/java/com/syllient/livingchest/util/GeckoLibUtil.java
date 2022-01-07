package com.syllient.livingchest.util;

import net.minecraft.entity.Entity;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;

@SuppressWarnings("rawtypes")
public class GeckoLibUtil {
  public static boolean isAnimationRunning(final String animationName, final AnimationController controller) {
    return GeckoLibUtil.isCurrentAnimation(animationName, controller)
        && controller.getAnimationState() == AnimationState.Running;
  }

  public static boolean isAnimationTransitioning(final String animationName, final AnimationController controller) {
    return GeckoLibUtil.isCurrentAnimation(animationName, controller)
        && controller.getAnimationState() == AnimationState.Transitioning;
  }

  public static boolean isAnimationStopped(final String animationName, final AnimationController controller) {
    return GeckoLibUtil.isCurrentAnimation(animationName, controller)
        && controller.getAnimationState() == AnimationState.Stopped;
  }

  public static boolean isCurrentAnimation(final String animationName, final AnimationController controller) {
    return controller.getCurrentAnimation() != null
        && controller.getCurrentAnimation().animationName.equals(animationName);
  }

  public static <T extends Entity & IAnimatable> AnimationController getEntityController(
      final T entity,
      final String controllerName) {
    return entity
        .getFactory()
        .getOrCreateAnimationData(entity.getUniqueID().hashCode())
        .getAnimationControllers()
        .get(controllerName);
  }
}
