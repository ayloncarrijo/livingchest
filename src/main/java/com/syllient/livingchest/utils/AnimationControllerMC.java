package com.syllient.livingchest.utils;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.easing.EasingType;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.snapshot.BoneSnapshot;
import software.bernie.shadowed.eliotlash.molang.MolangParser;

public class AnimationControllerMC<T extends IAnimatable> extends AnimationController<T> {
  private boolean hasJustFinishedAnimation = false;

  public AnimationControllerMC(T animatable, String name, float transitionLengthTicks,
      IAnimationPredicate<T> animationPredicate) {
    super(animatable, name, transitionLengthTicks, animationPredicate);
  }

  public AnimationControllerMC(T animatable, String name, float transitionLengthTicks,
      Function<Double, Double> customEasingMethod, IAnimationPredicate<T> animationPredicate) {
    super(animatable, name, transitionLengthTicks, customEasingMethod, animationPredicate);
  }

  public AnimationControllerMC(T animatable, String name, float transitionLengthTicks, EasingType easingtype,
      IAnimationPredicate<T> animationPredicate) {
    super(animatable, name, transitionLengthTicks, easingtype, animationPredicate);
  }

  @Override
  public void process(double tick, AnimationEvent<T> event, List<IBone> modelRendererList,
      HashMap<String, Pair<IBone, BoneSnapshot>> boneSnapshotCollection, MolangParser parser,
      boolean crashWhenCantFindBone) {
    this.hasJustFinishedAnimation = this.currentAnimation != null
        ? adjustTick(tick) >= this.currentAnimation.animationLength
        : false;

    super.process(tick, event, modelRendererList, boneSnapshotCollection, parser, crashWhenCantFindBone);
  }

  public boolean hasJustFinishedAnimation() {
    return this.hasJustFinishedAnimation;
  }
}
