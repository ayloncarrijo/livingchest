package com.syllient.livingchest.animation;

import software.bernie.geckolib3.core.IAnimatable;

public class EyeBoneAnimation<T extends IAnimatable> extends Animation<T> {
  public static class Animation {
    public static final String IDLE = "animation.eye_bone.idle";
  }

  public static class Controller {
    public static final String IDLE = "idle_controller";
  }

  public EyeBoneAnimation(final T animatable) {
    super(animatable);
  }
}
