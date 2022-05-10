package com.syllient.livingchest.animation;

import java.util.HashMap;
import java.util.LinkedHashMap;
import com.syllient.livingchest.animation.animator.Animator;
import com.syllient.livingchest.animation.controller.ExtendedAnimationController;
import com.syllient.livingchest.animation.controller.OrderedAnimationController;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;

@SuppressWarnings({"rawtypes", "unchecked"})
public class OrderedAnimationData<T extends IAnimatable> extends AnimationData {
  protected final HashMap<String, AnimationController> innerAnimationControllers =
      new LinkedHashMap<>();

  protected final HashMap<String, OrderedAnimationController<T>> orderedAnimationControllers =
      new LinkedHashMap<>();

  protected final Animator<T> animator;

  protected OrderedAnimationController<T> currentController;

  public OrderedAnimationData(final Animator<T> animator) {
    this.animator = animator;
    this.addAnimationController(new ExtendedAnimationController<>(animator.getAnimatable(),
        "SELECTOR", 0, animator::selectController));
  }

  @Override
  public AnimationController addAnimationController(final AnimationController controller) {
    this.innerAnimationControllers.put(controller.getName(), controller);

    if (controller instanceof OrderedAnimationController) {
      this.orderedAnimationControllers.put(controller.getName(),
          (OrderedAnimationController<T>) controller);
    }

    return controller;
  }

  @Override
  public HashMap<String, AnimationController> getAnimationControllers() {
    return this.innerAnimationControllers;
  }

  public HashMap<String, OrderedAnimationController<T>> getOrderedAnimationControllers() {
    return this.orderedAnimationControllers;
  }

  public OrderedAnimationController<T> getCurrentController() {
    return this.currentController;
  }

  public void setCurrentController(final OrderedAnimationController<T> currentController) {
    this.currentController = currentController;
  }
}
