package com.syllient.livingchest.animation.controller;

import java.util.function.Predicate;
import com.syllient.livingchest.animation.AnimationPhase;
import com.syllient.livingchest.animation.PhasedAnimation;
import com.syllient.livingchest.animation.animator.Animator;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class PhasedAnimationController<T extends IAnimatable>
    extends OrderedAnimationController<T> {
  protected boolean isCanceled = false;

  protected AnimationPhase animationPhase = AnimationPhase.END;

  protected final PhasedAnimation<T> phasedAnimation;

  public PhasedAnimationController(final Animator<T> animator, final String name,
      final Predicate<OrderedAnimationController<T>> shouldAnimate,
      final PhasedAnimation<T> phasedAnimation) {
    super(animator, name, shouldAnimate, null);
    this.phasedAnimation = phasedAnimation;
  }

  @Override
  public PlayState animate(final AnimationEvent<T> event) {
    if (this.animationPhase == AnimationPhase.END && !this.isAnimationFinished()) {
      return PlayState.CONTINUE;
    }

    if (this.animationPhase == AnimationPhase.START) {
      if (this.isAnimationFinished()) {
        this.setAnimation(phasedAnimation.getLoopAnimation(event));
        this.animationPhase = AnimationPhase.LOOP;
      }

      return PlayState.CONTINUE;
    }

    if (!this.isCanceled && this.shouldAnimate()) {
      if (this.animationPhase != AnimationPhase.LOOP) {
        this.setAnimation(phasedAnimation.getStartAnimation(event));
        this.animationPhase = AnimationPhase.START;
      }
    } else if (this.animationPhase == AnimationPhase.LOOP && this.isAnimationFinished()) {
      this.setAnimation(phasedAnimation.getEndAnimation(event));
      this.animationPhase = AnimationPhase.END;
      this.isCanceled = false;
    }

    return PlayState.CONTINUE;
  }

  @Override
  public boolean canStop() {
    if (this.animationPhase != AnimationPhase.END) {
      this.isCanceled = true;
      return false;
    }

    return super.canStop();
  }

  public boolean isCanceled() {
    return this.isCanceled;
  }

  public void setIsCanceled() {
    this.isCanceled = true;
  }

  public AnimationPhase getAnimationPhase() {
    return this.animationPhase;
  }

  protected void setAnimationPhase(final AnimationPhase animationPhase) {
    this.animationPhase = animationPhase;
  }
}
