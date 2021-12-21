package com.syllient.livingchest.entity;

import java.util.Optional;

import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.AnimationState;
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
  private static final String ANIMATION_IDLE = "animation.chester.idle";
  private static final String ANIMATION_START_JUMP = "animation.chester.start_jump";
  private static final String ANIMATION_JUMP = "animation.chester.jump";
  private static final String ANIMATION_STOP_JUMP = "animation.chester.stop_jump";

  private static final DataParameter<Boolean> IS_MOUTH_OPEN = EntityDataManager.<Boolean>createKey(EntityChester.class,
      DataSerializers.BOOLEAN);
  private static final DataParameter<Boolean> WILL_JUMP = EntityDataManager.<Boolean>createKey(
      EntityChester.class,
      DataSerializers.BOOLEAN);

  private AnimationFactory factory = new AnimationFactory(this);
  private AnimationController<EntityChester> idleController;
  private AnimationController<EntityChester> jumpController;
  private AnimationController<EntityChester> mouthController;

  private int ticksOfLastMouthInteract = 0;

  public EntityChester(World worldIn) {
    super(worldIn);
    this.ignoreFrustumCheck = true;
  }

  private <E extends IAnimatable> PlayState idlePredicate(AnimationEvent<E> event) {
    boolean isIdling = this.jumpController.getAnimationState() == AnimationState.Stopped;

    if (!isIdling) {
      return PlayState.STOP;
    }

    event.getController()
        .setAnimation(
            new AnimationBuilder()
                .addAnimation(ANIMATION_IDLE, true));

    return PlayState.CONTINUE;
  }

  private <E extends IAnimatable> PlayState jumpPredicate(AnimationEvent<E> event) {
    if (event.isMoving()) {
      event.getController().setAnimation(
          new AnimationBuilder().addAnimation(ANIMATION_START_JUMP).addAnimation(ANIMATION_JUMP,
              true));
    } else {
      event.getController().clearAnimationCache();
      return PlayState.STOP;
      // Optional.ofNullable(event.getController().getCurrentAnimation()).ifPresent((animation)
      // -> {
      // if (animation.animationName.equals(ANIMATION_JUMP)) {
      // event.getController().setAnimation(new
      // AnimationBuilder().addAnimation(ANIMATION_STOP_JUMP));
      // }
      // });
    }

    return PlayState.CONTINUE;
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
    this.idleController = new AnimationController<EntityChester>(this, "idle_controller", 0, this::idlePredicate);
    this.jumpController = new AnimationController<EntityChester>(this, "jump_controller", 0, this::jumpPredicate);
    this.mouthController = new AnimationController<EntityChester>(this, "mouth_controller", 0, this::mouthPredicate);

    // data.addAnimationController(this.idleController);
    data.addAnimationController(this.jumpController);
    // data.addAnimationController(this.mouthController);
  }

  @Override
  public AnimationFactory getFactory() {
    return this.factory;
  }

  @Override
  protected void entityInit() {
    super.entityInit();
    this.dataManager.register(IS_MOUTH_OPEN, false);
    this.dataManager.register(WILL_JUMP, false);
  }

  public boolean willJump() {
    return this.dataManager.get(WILL_JUMP);
  }

  public void setWillJump(boolean value) {
    this.dataManager.set(WILL_JUMP, value);
  }

  public boolean isMouthOpen() {
    return this.dataManager.get(IS_MOUTH_OPEN);
  }

  public void setIsMouthOpen(boolean value) {
    if (this.ticksExisted - this.ticksOfLastMouthInteract > 10) {
      this.dataManager.set(IS_MOUTH_OPEN, value);
      this.ticksOfLastMouthInteract = this.ticksExisted;
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

  @Override
  protected float getJumpUpwardsMotion() {
    return 0.42F;
  }

  public int getJumpDelay() {
    return 5;
  }

  public float getMoveSpeed() {
    return 0.85F;
  }

  @Override
  public void onUpdate() {
    super.onUpdate();
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

    public ChesterMoveHelper(EntityChester chesterIn) {
      super(chesterIn);
      this.chester = chesterIn;
    }

    @Override
    public void onUpdateMoveHelper() {
      if (this.chester.isMouthOpen()) {
        this.chester.setAIMoveSpeed(0.0F);
        return;
      }

      float sourceAngle = this.chester.rotationYaw;
      float targetAngle = (float) (MathHelper.atan2(
          this.posZ - this.chester.posZ,
          this.posX - this.chester.posX)
          * (180D / Math.PI)) - 90.0F;

      this.chester.rotationYaw = this.limitAngle(sourceAngle, targetAngle, this.chester.onGround ? 90.0F : 15.0F);
      this.chester.rotationYawHead = this.chester.rotationYaw;
      this.chester.renderYawOffset = this.chester.rotationYaw;

      if (this.action != EntityMoveHelper.Action.MOVE_TO) {
        this.chester.setAIMoveSpeed(0.0F);
      } else {
        this.action = EntityMoveHelper.Action.WAIT;

        if (this.chester.willJump()) {
          this.chester.setWillJump(false);
          this.chester.setAIMoveSpeed(this.chester.getMoveSpeed());
          this.chester.getJumpHelper().setJumping();
          return;
        }

        if (this.chester.onGround && this.jumpDelay-- <= 0) {
          this.chester.setWillJump(true);
          this.chester.setAIMoveSpeed(0.0F);
          this.jumpDelay = this.chester.getJumpDelay();
          return;
        }

        this.chester.setAIMoveSpeed(0.0F);
      }
    }
  }
}
