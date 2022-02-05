package com.syllient.livingchest.util;

import com.syllient.livingchest.geckolib.ExtendedAnimationController;
import net.minecraft.entity.Entity;
import software.bernie.geckolib3.core.IAnimatable;

@SuppressWarnings("unchecked")
public class GeckoLibUtil {
  public static <T extends Entity & IAnimatable> ExtendedAnimationController<T> getController(
      final T entity, final String controllerName) {
    return (ExtendedAnimationController<T>) entity.getFactory()
        .getOrCreateAnimationData(entity.getUniqueID().hashCode()).getAnimationControllers()
        .get(controllerName);
  }
}
