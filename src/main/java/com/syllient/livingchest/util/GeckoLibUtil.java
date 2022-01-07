package com.syllient.livingchest.util;

import net.minecraft.entity.Entity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;

@SuppressWarnings("rawtypes")
public class GeckoLibUtil {
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
