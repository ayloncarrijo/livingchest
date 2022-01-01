package com.syllient.livingchest.entity;

import java.util.HashMap;
import java.util.List;

import com.syllient.livingchest.utils.AnimationUtil;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.snapshot.BoneSnapshot;
import software.bernie.shadowed.eliotlash.molang.MolangParser;

class AnimationControllerTest<T extends IAnimatable> extends AnimationController<T> {
  public AnimationControllerTest(T animatable, String name, float transitionLengthTicks,
      IAnimationPredicate<T> animationPredicate) {
    super(animatable, name, transitionLengthTicks, animationPredicate);
  }

  @Override
  public void process(double tick, AnimationEvent<T> event, List<IBone> modelRendererList,
      HashMap<String, Pair<IBone, BoneSnapshot>> boneSnapshotCollection, MolangParser parser,
      boolean crashWhenCantFindBone) {
    super.process(tick, event, modelRendererList, boneSnapshotCollection, parser, crashWhenCantFindBone);
  }
}

public class EntityChester extends EntityCow implements IAnimatable {
  private static final String ANIMATION_IDLE = "animation.chester.idle";
  private static final String ANIMATION_OPEN_MOUTH = "animation.chester.open_mouth";
  private static final String ANIMATION_IDLE_MOUTH = "animation.chester.idle_mouth";
  private static final String ANIMATION_CLOSE_MOUTH = "animation.chester.close_mouth";
  private static final String ANIMATION_INIT_JUMP = "animation.chester.init_jump";
  private static final String ANIMATION_JUMP = "animation.chester.jump";
  private static final String ANIMATION_STOP_JUMP = "animation.chester.stop_jump";

  private static final DataParameter<Boolean> IS_MOUTH_OPEN = EntityDataManager.<Boolean>createKey(EntityChester.class,
      DataSerializers.BOOLEAN);

  private AnimationFactory factory = new AnimationFactory(this);
  private int ticksIdling = 0;

  public EntityChester(World worldIn) {
    super(worldIn);
    this.ignoreFrustumCheck = true;
  }

  @Override
  protected void entityInit() {
    super.entityInit();
    this.dataManager.register(IS_MOUTH_OPEN, false);
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(450.0D);
    this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.27D);
  }

  @Override
  public AnimationFactory getFactory() {
    return this.factory;
  }

  @Override
  public void registerControllers(AnimationData data) {
    AnimationController<EntityChester> idleController = new AnimationController<EntityChester>(this,
        "idle_controller", 0, this::idlePredicate);

    AnimationController<EntityChester> jumpController = new AnimationController<EntityChester>(this,
        "jump_controller", 0, this::jumpPredicate);
    jumpController.registerSoundListener(this::jumpKeyframes);

    AnimationController<EntityChester> mouthController = new AnimationController<EntityChester>(this,
        "mouth_controller", 0, this::mouthPredicate);

    data.addAnimationController(idleController);
    data.addAnimationController(jumpController);
    data.addAnimationController(mouthController);
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

  private PlayState idlePredicate(AnimationEvent<? extends IAnimatable> event) {
    boolean isIdling = AnimationUtil
        .getEntityController(this, "jump_controller")
        .getAnimationState() == AnimationState.Stopped;

    if (!isIdling) {
      this.ticksIdling = 0;
      return PlayState.STOP;
    }

    this.ticksIdling += 1;

    if (this.ticksIdling < 10) {
      return PlayState.STOP;
    }

    event.getController()
        .setAnimation(
            new AnimationBuilder()
                .addAnimation(ANIMATION_IDLE, true));

    return PlayState.CONTINUE;
  }

  private PlayState jumpPredicate(AnimationEvent<? extends IAnimatable> event) {
    if (AnimationUtil.isAnimationStopped(ANIMATION_INIT_JUMP, event.getController())) {
      event.getController().setAnimation(
          new AnimationBuilder()
              .addAnimation(ANIMATION_JUMP, true));

      return PlayState.CONTINUE;
    }

    boolean isJumping = AnimationUtil.isCurrentAnimation(ANIMATION_JUMP, event.getController());

    if (event.isMoving()) {
      if (!isJumping) {
        event.getController().setAnimation(
            new AnimationBuilder()
                .addAnimation(ANIMATION_INIT_JUMP));
      }
    } else if (isJumping && AnimationUtil.hasReachedKeyframe("query.anim_end", event.getController())) {
      event.getController().setAnimation(
          new AnimationBuilder()
              .addAnimation(ANIMATION_STOP_JUMP));
    }

    return PlayState.CONTINUE;
  }

  private PlayState mouthPredicate(AnimationEvent<? extends IAnimatable> event) {
    if (this.isMouthOpen()) {
      event.getController().setAnimation(
          new AnimationBuilder()
              .addAnimation(ANIMATION_OPEN_MOUTH)
              .addAnimation(ANIMATION_IDLE_MOUTH, true));
    } else if (AnimationUtil.isCurrentAnimation(ANIMATION_IDLE_MOUTH, event.getController())) {
      event.getController().setAnimation(
          new AnimationBuilder()
              .addAnimation(ANIMATION_CLOSE_MOUTH));
    }

    return PlayState.CONTINUE;
  }

  private void jumpKeyframes(SoundKeyframeEvent<? extends IAnimatable> event) {
    System.out.println("aaaaa");
  }

  public boolean isMouthOpen() {
    return this.dataManager.get(IS_MOUTH_OPEN);
  }

  public void setIsMouthOpen(boolean value) {
    this.dataManager.set(IS_MOUTH_OPEN, value);
  }

  public void openMouth() {
    this.setIsMouthOpen(true);
  }

  public void closeMouth() {
    this.setIsMouthOpen(false);
  }

  public void toggleMouth() {
    if (this.isMouthOpen()) {
      this.closeMouth();
    } else {
      this.openMouth();
    }
  }

  @Override
  protected float getJumpUpwardsMotion() {
    return 0.5F;
  }
}
