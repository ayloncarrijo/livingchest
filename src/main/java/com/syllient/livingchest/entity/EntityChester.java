package com.syllient.livingchest.entity;

import com.syllient.livingchest.GuiHandler;
import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.gecko.AnimationControllerExtended;
import com.syllient.livingchest.inventory.InventoryChester;
import com.syllient.livingchest.util.AnimationUtil;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
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
  private static final String ANIMATION_IDLE = "animation.chester.idle";
  private static final String ANIMATION_OPEN_MOUTH = "animation.chester.open_mouth";
  private static final String ANIMATION_IDLE_MOUTH = "animation.chester.idle_mouth";
  private static final String ANIMATION_CLOSE_MOUTH = "animation.chester.close_mouth";
  private static final String ANIMATION_INIT_JUMP = "animation.chester.init_jump";
  private static final String ANIMATION_JUMP = "animation.chester.jump";
  private static final String ANIMATION_STOP_JUMP = "animation.chester.stop_jump";

  private static final DataParameter<Boolean> IS_MOUTH_OPEN = EntityDataManager.createKey(EntityChester.class,
      DataSerializers.BOOLEAN);

  private final InventoryChester inventory = new InventoryChester(this, 27);
  private final AnimationFactory factory = new AnimationFactory(this);

  private int moveSpeedResetTimer = 0;
  private int ticksIdling = 0;

  public EntityChester(final World worldIn) {
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
    this.addPotionEffect(
        new PotionEffect(
            MobEffects.REGENERATION,
            Integer.MAX_VALUE,
            2,
            false,
            false));
    this.setMoveSpeed(this.getDefaultMoveSpeed());
    this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
        .setBaseValue(450.0D);

  }

  @Override
  public AnimationFactory getFactory() {
    return this.factory;
  }

  @Override
  public void registerControllers(final AnimationData data) {
    final AnimationController<EntityChester> idleController = new AnimationController<EntityChester>(this,
        "idle_controller", 0, this::idlePredicate);

    final AnimationController<EntityChester> jumpController = new AnimationControllerExtended<EntityChester>(this,
        "jump_controller", 0, this::jumpPredicate);

    final AnimationController<EntityChester> mouthController = new AnimationController<EntityChester>(this,
        "mouth_controller", 0, this::mouthPredicate);

    data.addAnimationController(idleController);
    data.addAnimationController(jumpController);
    data.addAnimationController(mouthController);
  }

  @Override
  public void onUpdate() {
    if (!this.world.isRemote) {
      if (!this.isMouthOpen()
          && this.moveSpeedResetTimer > 0
          && --this.moveSpeedResetTimer == 0) {
        this.setMoveSpeed(this.getDefaultMoveSpeed());
      }
    }

    super.onUpdate();
  }

  @Override
  protected void damageEntity(final DamageSource damageSrc, final float damageAmount) {
    super.damageEntity(damageSrc, damageAmount);
  }

  @Override
  public boolean processInteract(final EntityPlayer player, final EnumHand hand) {
    if (!this.world.isRemote && hand == EnumHand.MAIN_HAND) {
      player.openGui(
          LivingChest.instance,
          GuiHandler.ID_GUI_CHESTER,
          this.world,
          this.getEntityId(),
          0,
          0);

      return true;
    }

    return false;
  }

  private PlayState idlePredicate(final AnimationEvent<? extends IAnimatable> event) {
    final boolean isIdling = AnimationUtil
        .getEntityController(this, "jump_controller")
        .getAnimationState() == AnimationState.Stopped;

    if (isIdling) {
      this.ticksIdling += 1;
    } else {
      this.ticksIdling = 0;
    }

    if (this.ticksIdling < 10) {
      return PlayState.STOP;
    }

    event.getController()
        .setAnimation(
            new AnimationBuilder()
                .addAnimation(ANIMATION_IDLE, true));

    return PlayState.CONTINUE;
  }

  @SuppressWarnings("rawtypes")
  private PlayState jumpPredicate(final AnimationEvent<? extends IAnimatable> event) {
    if (AnimationUtil.isAnimationStopped(ANIMATION_INIT_JUMP, event.getController())) {
      event.getController().setAnimation(
          new AnimationBuilder()
              .addAnimation(ANIMATION_JUMP, true));

      return PlayState.CONTINUE;
    }

    final boolean isJumping = AnimationUtil.isCurrentAnimation(ANIMATION_JUMP, event.getController());

    if (event.isMoving()) {
      if (!isJumping) {
        event.getController().setAnimation(
            new AnimationBuilder()
                .addAnimation(ANIMATION_INIT_JUMP));
      }
    } else if (isJumping && ((AnimationControllerExtended) event.getController()).isAnimationFinished()) {
      event.getController().setAnimation(
          new AnimationBuilder()
              .addAnimation(ANIMATION_STOP_JUMP));
    }

    return PlayState.CONTINUE;
  }

  private PlayState mouthPredicate(final AnimationEvent<? extends IAnimatable> event) {
    if (this.ticksIdling < 10) {
      return PlayState.STOP;
    }

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

  public boolean isMouthOpen() {
    return this.dataManager.get(IS_MOUTH_OPEN);
  }

  private void setIsMouthOpen(final boolean value) {
    this.dataManager.set(IS_MOUTH_OPEN, value);
  }

  public void openMouth() {
    this.setIsMouthOpen(true);
    this.setMoveSpeed(0);
  }

  public void closeMouth() {
    this.setIsMouthOpen(false);
    this.moveSpeedResetTimer = 10;
  }

  public InventoryChester getInventory() {
    return this.inventory;
  }

  public void setMoveSpeed(final double value) {
    this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
        .setBaseValue(value);
  }

  public double getDefaultMoveSpeed() {
    return 0.25D;
  }

  @Override
  protected float getJumpUpwardsMotion() {
    return 0.5F;
  }
}
