package com.syllient.livingchest.entity;

import java.util.Optional;

import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class EntityChester extends EntityCow implements IAnimatable {
  private static final String ANIMATION_IDLE_MOUTH = "animation.chester.idle_mouth";
  private static final String ANIMATION_OPEN_MOUTH = "animation.chester.open_mouth";
  private static final String ANIMATION_CLOSE_MOUTH = "animation.chester.close_mouth";

  private static final DataParameter<Boolean> IS_MOUTH_OPEN = EntityDataManager.<Boolean>createKey(EntityChester.class,
      DataSerializers.BOOLEAN);

  private AnimationFactory factory = new AnimationFactory(this);

  private int lastMouthInteract = 0;

  public EntityChester(World worldIn) {
    super(worldIn);
    this.moveHelper = new EntityChester.ChesterMoveHelper(this);
    this.ignoreFrustumCheck = true;
  }

  private <E extends IAnimatable> PlayState mouthPredicate(AnimationEvent<E> event) {
    if (this.isMouthOpen()) {
      event.getController().setAnimation(
          new AnimationBuilder()
              .addAnimation(ANIMATION_OPEN_MOUTH)
              .addAnimation(ANIMATION_IDLE_MOUTH, true));
    } else {
      Optional.ofNullable(event.getController().getCurrentAnimation()).ifPresent((animation) -> {
        if (animation.animationName.equals(ANIMATION_IDLE_MOUTH)) {
          event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIMATION_CLOSE_MOUTH));
        }
      });
      ;
    }

    return PlayState.CONTINUE;
  }

  @Override
  public void registerControllers(AnimationData data) {
    data.addAnimationController(
        new AnimationController<EntityChester>(this, "mouth_controller", 0, this::mouthPredicate));
  }

  @Override
  public AnimationFactory getFactory() {
    return this.factory;
  }

  @Override
  protected void entityInit() {
    super.entityInit();
    this.dataManager.register(IS_MOUTH_OPEN, false);
  }

  public boolean isMouthOpen() {
    return this.dataManager.get(IS_MOUTH_OPEN);
  }

  public void setIsMouthOpen(boolean value) {
    if (this.ticksExisted - this.lastMouthInteract > 10) {
      this.dataManager.set(IS_MOUTH_OPEN, value);
      this.lastMouthInteract = this.ticksExisted;
    }
  }

  public void openMouth() {
    this.setIsMouthOpen(true);
  }

  public void closeMouth() {
    this.setIsMouthOpen(false);
  }

  public void toggleMouth() {
    this.setIsMouthOpen(!this.isMouthOpen());
  }

  public void setMovementSpeed(double speed) {
    this.getNavigator().setSpeed(speed);
    this.moveHelper.setMoveTo(this.moveHelper.getX(), this.moveHelper.getY(), this.moveHelper.getZ(), speed);
  }

  @Override
  protected float getJumpUpwardsMotion() {
    return 0.5F;
  }

  public int getJumpDelay() {
    return 10;
  }

  @Override
  public boolean processInteract(EntityPlayer player, EnumHand hand) {
    if (!player.world.isRemote && hand == EnumHand.MAIN_HAND) {
      this.toggleMouth();

      return true;
    }

    return false;
  }

  static class ChesterMoveHelper extends EntityMoveHelper {
    private final EntityChester chester;
    private int jumpDelay;

    public ChesterMoveHelper(EntityChester chester) {
      super(chester);
      this.chester = chester;
      this.jumpDelay = chester.getJumpDelay();
    }

    @Override
    public void onUpdateMoveHelper() {
      if (!this.isUpdating()) {
        this.chester.setMovementSpeed(0.0D);
      } else {
        if (this.chester.onGround && this.jumpDelay-- <= 0) {
          this.jumpDelay = this.chester.getJumpDelay();
          this.chester.setMovementSpeed(6.0D);
          this.chester.getJumpHelper().setJumping();
        } else {
          this.chester.setMovementSpeed(0.0D);
        }
      }

      super.onUpdateMoveHelper();
    }
  }
}
