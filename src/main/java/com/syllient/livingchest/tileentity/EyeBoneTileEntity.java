package com.syllient.livingchest.tileentity;

import com.syllient.livingchest.geckolib.ExtendedAnimationController;
import net.minecraft.tileentity.TileEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class EyeBoneTileEntity extends TileEntity implements IAnimatable {
  private static class Animation {
    private static final String IDLE = "animation.eye_bone.idle";
  }

  private static class Controller {
    private static final String IDLE = "idle_controller";
  }

  private final AnimationFactory factory = new AnimationFactory(this);

  private PlayState idlePredicate(final AnimationEvent<? extends IAnimatable> event) {
    event.getController().setAnimation(new AnimationBuilder().addAnimation(Animation.IDLE, true));

    return PlayState.CONTINUE;
  }

  @Override
  public void registerControllers(final AnimationData data) {
    data.addAnimationController(
        new ExtendedAnimationController<>(this, Controller.IDLE, 0, this::idlePredicate));
  }

  @Override
  public AnimationFactory getFactory() {
    return this.factory;
  }
}
