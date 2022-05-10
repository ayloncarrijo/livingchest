package com.syllient.livingchest.animation;

import java.util.HashMap;
import com.syllient.livingchest.animation.animator.Animator;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class OrderedAnimationFactory<T extends IAnimatable> extends AnimationFactory {
  private final Animator<T> animator;
  private final HashMap<Integer, OrderedAnimationData<T>> animationDataById = new HashMap<>();

  public OrderedAnimationFactory(final Animator<T> animator) {
    super(animator.getAnimatable());
    this.animator = animator;
  }

  @Override
  public OrderedAnimationData<T> getOrCreateAnimationData(final Integer uniqueId) {
    return this.animationDataById.computeIfAbsent(uniqueId, (key) -> {
      final OrderedAnimationData<T> animationData = new OrderedAnimationData<>(this.animator);
      this.animator.getAnimatable().registerControllers(animationData);

      return animationData;
    });
  }
}
