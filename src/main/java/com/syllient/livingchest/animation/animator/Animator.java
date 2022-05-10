package com.syllient.livingchest.animation.animator;

import com.syllient.livingchest.animation.OrderedAnimationData;
import com.syllient.livingchest.animation.OrderedAnimationFactory;
import com.syllient.livingchest.animation.controller.ExtendedAnimationController;
import com.syllient.livingchest.animation.controller.OrderedAnimationController;
import net.minecraft.entity.LivingEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public abstract class Animator<T extends IAnimatable> {
  protected final T animatable;

  protected final OrderedAnimationFactory<T> factory;

  public Animator(final T animatable) {
    this.animatable = animatable;
    this.factory = new OrderedAnimationFactory<T>(this);
  }

  @SuppressWarnings("unchecked")
  public PlayState interceptController(final AnimationEvent<T> event) {
    final OrderedAnimationData<T> animationData = this.getAnimationData();

    final OrderedAnimationController<T> currentController = animationData.getCurrentController();

    final OrderedAnimationController<T> eventController =
        (OrderedAnimationController<T>) event.getController();

    if (currentController != null && !currentController.equals(eventController)
        && !currentController.canRunInParallel(eventController)) {
      return PlayState.STOP;
    }

    return eventController.animate(event);
  }

  public PlayState selectController(final AnimationEvent<T> event) {
    final OrderedAnimationData<T> animationData = this.getAnimationData();

    final OrderedAnimationController<T> currentController = animationData.getCurrentController();

    final OrderedAnimationController<T> wantedController =
        animationData.getOrderedAnimationControllers().values().stream()
            .filter(OrderedAnimationController::shouldAnimate).findFirst().orElse(null);

    final double currentTick =
        ((ExtendedAnimationController<?>) event.getController()).getCurrentTick();

    animationData.getOrderedAnimationControllers()
        .forEach((key, controller) -> controller.setCurrentTick(currentTick));

    if (this.canChangeController(currentController, wantedController)) {
      animationData.setCurrentController(wantedController);
    }

    this.tick();

    return PlayState.CONTINUE;
  }

  public void tick() {}

  public OrderedAnimationData<T> getAnimationData() {
    return this.factory.getOrCreateAnimationData(this.getUniqueId());
  }

  public Integer getUniqueId() {
    if (this.animatable instanceof LivingEntity) {
      return ((LivingEntity) this.animatable).getUUID().hashCode();
    }

    return this.animatable.hashCode();
  }

  public T getAnimatable() {
    return this.animatable;
  }

  public AnimationFactory getFactory() {
    return this.factory;
  }

  protected boolean canChangeController(final OrderedAnimationController<T> currentController,
      final OrderedAnimationController<T> wantedController) {
    if (currentController == null) {
      return true;
    }

    if (currentController.equals(wantedController)) {
      return false;
    }

    if (wantedController != null && wantedController.canRunInParallel(currentController)) {
      return true;
    }

    return currentController.canStop();
  }

  public abstract void registerControllers(AnimationData data);
}
