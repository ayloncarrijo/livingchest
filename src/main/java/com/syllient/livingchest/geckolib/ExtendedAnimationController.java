package com.syllient.livingchest.geckolib;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import org.apache.commons.lang3.tuple.Pair;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.easing.EasingType;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.snapshot.BoneSnapshot;
import software.bernie.shadowed.eliotlash.molang.MolangParser;

public class ExtendedAnimationController<T extends IAnimatable> extends AnimationController<T> {
  private boolean hasJustFinishedAnimation = false;

  public ExtendedAnimationController(final T animatable, final String name,
      final float transitionLengthTicks, final IAnimationPredicate<T> animationPredicate) {
    super(animatable, name, transitionLengthTicks, animationPredicate);
  }

  public ExtendedAnimationController(final T animatable, final String name,
      final float transitionLengthTicks, final Function<Double, Double> customEasingMethod,
      final IAnimationPredicate<T> animationPredicate) {
    super(animatable, name, transitionLengthTicks, customEasingMethod, animationPredicate);
  }

  public ExtendedAnimationController(final T animatable, final String name,
      final float transitionLengthTicks, final EasingType easingtype,
      final IAnimationPredicate<T> animationPredicate) {
    super(animatable, name, transitionLengthTicks, easingtype, animationPredicate);
  }

  @Override
  public void process(final double tick, final AnimationEvent<T> event,
      final List<IBone> modelRendererList,
      final HashMap<String, Pair<IBone, BoneSnapshot>> boneSnapshotCollection,
      final MolangParser parser, final boolean crashWhenCantFindBone) {
    this.hasJustFinishedAnimation =
        !this.isAnimationStopped() && adjustTick(tick) >= this.currentAnimation.animationLength;

    super.process(tick, event, modelRendererList, boneSnapshotCollection, parser,
        crashWhenCantFindBone);
  }

  public boolean isAnimationTransitioning() {
    return this.getAnimationState() == AnimationState.Transitioning;
  }

  public boolean isAnimationTransitioning(final String animationName) {
    return this.isCurrentAnimation(animationName) && this.isAnimationTransitioning();
  }

  public boolean isAnimationRunning() {
    return this.getAnimationState() == AnimationState.Running;
  }

  public boolean isAnimationRunning(final String animationName) {
    return this.isCurrentAnimation(animationName) && this.isAnimationRunning();
  }

  public boolean isAnimationStopped() {
    return this.getAnimationState() == AnimationState.Stopped;
  }

  public boolean isAnimationStopped(final String animationName) {
    return this.isCurrentAnimation(animationName) && this.isAnimationStopped();
  }

  public boolean isAnimationLooping() {
    return this.getCurrentAnimation() != null && this.getCurrentAnimation().loop;
  }

  public boolean isCurrentAnimation(final String animationName) {
    return this.getCurrentAnimation() != null
        && this.getCurrentAnimation().animationName.equals(animationName);
  }

  public boolean hasJustFinishedAnimation() {
    return this.hasJustFinishedAnimation;
  }
}
