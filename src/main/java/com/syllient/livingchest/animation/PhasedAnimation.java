package com.syllient.livingchest.animation;

import java.util.function.Function;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class PhasedAnimation<T extends IAnimatable> {
  private final Function<AnimationEvent<T>, AnimationBuilder> startAnimation;
  private final Function<AnimationEvent<T>, AnimationBuilder> loopAnimation;
  private final Function<AnimationEvent<T>, AnimationBuilder> endAnimation;

  private PhasedAnimation(final Function<AnimationEvent<T>, AnimationBuilder> startAnimation,
      final Function<AnimationEvent<T>, AnimationBuilder> loopAnimation,
      final Function<AnimationEvent<T>, AnimationBuilder> endAnimation) {
    this.startAnimation = startAnimation;
    this.loopAnimation = loopAnimation;
    this.endAnimation = endAnimation;
  }

  public AnimationBuilder getStartAnimation(final AnimationEvent<T> event) {
    return this.startAnimation.apply(event);
  }

  public AnimationBuilder getLoopAnimation(final AnimationEvent<T> event) {
    return this.loopAnimation.apply(event);
  }

  public AnimationBuilder getEndAnimation(final AnimationEvent<T> event) {
    return this.endAnimation.apply(event);
  }

  public static <T extends IAnimatable> StartAnimationBuilder<T> builder() {
    return new Builder<T>();
  }

  private static class Builder<T extends IAnimatable>
      implements StartAnimationBuilder<T>, LoopAnimationBuilder<T>, EndAnimationBuilder<T> {
    private Function<AnimationEvent<T>, AnimationBuilder> startAnimation;
    private Function<AnimationEvent<T>, AnimationBuilder> loopAnimation;
    private Function<AnimationEvent<T>, AnimationBuilder> endAnimation;

    public LoopAnimationBuilder<T> setStartAnimation(
        final Function<AnimationEvent<T>, AnimationBuilder> startAnimation) {
      this.startAnimation = startAnimation;
      return this;
    }

    public EndAnimationBuilder<T> setLoopAnimation(
        final Function<AnimationEvent<T>, AnimationBuilder> loopAnimation) {
      this.loopAnimation = loopAnimation;
      return this;
    }

    public PhasedAnimation<T> setEndAnimation(
        final Function<AnimationEvent<T>, AnimationBuilder> endAnimation) {
      this.endAnimation = endAnimation;
      return new PhasedAnimation<>(this.startAnimation, this.loopAnimation, this.endAnimation);
    }
  }

  public interface StartAnimationBuilder<T extends IAnimatable> {
    public LoopAnimationBuilder<T> setStartAnimation(
        Function<AnimationEvent<T>, AnimationBuilder> startAnimation);
  }

  public interface LoopAnimationBuilder<T extends IAnimatable> {
    public EndAnimationBuilder<T> setLoopAnimation(
        Function<AnimationEvent<T>, AnimationBuilder> loopAnimation);
  }

  public interface EndAnimationBuilder<T extends IAnimatable> {
    public PhasedAnimation<T> setEndAnimation(
        Function<AnimationEvent<T>, AnimationBuilder> endAnimation);
  }
}
