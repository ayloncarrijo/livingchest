package com.syllient.livingchest.animation.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import com.syllient.livingchest.animation.animator.Animator;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class OrderedAnimationController<T extends IAnimatable>
    extends ExtendedAnimationController<T> {
  protected final Predicate<OrderedAnimationController<T>> shouldAnimate;

  protected final Function<AnimationEvent<T>, AnimationBuilder> animationBuilder;

  protected final Set<String> parallelControllers = new HashSet<>();

  public OrderedAnimationController(final Animator<T> animator, final String name,
      final Predicate<OrderedAnimationController<T>> shouldAnimate,
      final Function<AnimationEvent<T>, AnimationBuilder> animationBuilder) {
    super(animator.getAnimatable(), name, 0, animator::interceptController);
    this.shouldAnimate = shouldAnimate;
    this.animationBuilder = animationBuilder;
  }

  public PlayState animate(final AnimationEvent<T> event) {
    this.setAnimation(animationBuilder.apply(event));
    return PlayState.CONTINUE;
  }

  public boolean shouldAnimate() {
    return this.shouldAnimate.test(this);
  }

  public boolean canStop() {
    return this.isAnimationFinished();
  }

  public boolean canRunInParallel(final String controllerName) {
    return this.parallelControllers.contains(controllerName);
  }

  public boolean canRunInParallel(final AnimationController<?> controller) {
    return this.canRunInParallel(controller.getName());
  }

  public OrderedAnimationController<T> addParallelController(final String controllerName) {
    this.parallelControllers.add(controllerName);
    return this;
  }
}
