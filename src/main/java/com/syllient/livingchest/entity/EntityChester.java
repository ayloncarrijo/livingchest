package com.syllient.livingchest.entity;

import java.util.HashMap;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.ModGuiHandler;
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
  private static class Animations {
    private static final String IDLE = "animation.chester.idle";

    private static final String INIT_JUMP = "animation.chester.init_jump";
    private static final String JUMP = "animation.chester.jump";
    private static final String STOP_JUMP = "animation.chester.stop_jump";

    private static final String INIT_JUMP_MOUTH = "animation.chester.init_jump_mouth";
    private static final String JUMP_MOUTH = "animation.chester.jump_mouth";
    private static final String STOP_JUMP_MOUTH = "animation.chester.stop_jump_mouth";

    private static final String OPEN_MOUTH = "animation.chester.open_mouth";
    private static final String IDLE_MOUTH = "animation.chester.idle_mouth";
    private static final String CLOSE_MOUTH = "animation.chester.close_mouth";
  }

  private static class Controllers {
    private static final String IDLE = "idle_controller";
    private static final String JUMP = "jump_controller";
    private static final String MOUTH = "mouth_controller";
  }

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
    this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
        .setBaseValue(450.0D);
    this.addPotionEffect(
        new PotionEffect(
            MobEffects.REGENERATION,
            Integer.MAX_VALUE,
            2,
            false,
            false));
    this.setMoveSpeed(this.getDefaultMoveSpeed());
  }

  @Override
  public AnimationFactory getFactory() {
    return this.factory;
  }

  @Override
  public void registerControllers(final AnimationData data) {
    final AnimationController<EntityChester> idleController = new AnimationController<EntityChester>(this,
        Controllers.IDLE, 0, this::idlePredicate);

    final AnimationController<EntityChester> jumpController = new AnimationControllerExtended<EntityChester>(this,
        Controllers.JUMP, 0, this::jumpPredicate);

    final AnimationController<EntityChester> mouthController = new AnimationController<EntityChester>(this,
        Controllers.MOUTH, 0, this::mouthPredicate);

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
      this.openGuiTo(player);
      return true;
    }

    return false;
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

  public void openGuiTo(final EntityPlayer player) {
    player.openGui(
        LivingChest.instance,
        ModGuiHandler.Gui.CHESTER,
        this.world,
        this.getEntityId(),
        0,
        0);
  }

  public double getDefaultMoveSpeed() {
    return 0.25D;
  }

  public void setMoveSpeed(final double value) {
    this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
        .setBaseValue(value);
  }

  @Override
  protected float getJumpUpwardsMotion() {
    return 0.5F;
  }

  public InventoryChester getInventory() {
    return this.inventory;
  }

  private PlayState idlePredicate(final AnimationEvent<? extends IAnimatable> event) {
    final boolean isIdling = AnimationUtil
        .getEntityController(this, Controllers.JUMP)
        .getAnimationState() == AnimationState.Stopped;

    if (isIdling) {
      this.ticksIdling += 1;
    } else {
      this.ticksIdling = 0;
    }

    if (this.ticksIdling < 5) {
      return PlayState.STOP;
    }

    event.getController()
        .setAnimation(
            new AnimationBuilder()
                .addAnimation(Animations.IDLE, true));

    return PlayState.CONTINUE;
  }

  @SuppressWarnings("rawtypes")
  private PlayState jumpPredicate(final AnimationEvent<? extends IAnimatable> event) {
    if (AnimationUtil.isAnimationStopped(Animations.INIT_JUMP, event.getController())) {
      event.getController().setAnimation(
          new AnimationBuilder()
              .addAnimation(Animations.JUMP, true));

      return PlayState.CONTINUE;
    }

    final boolean isJumping = AnimationUtil.isCurrentAnimation(Animations.JUMP, event.getController());

    if (event.isMoving()) {
      if (!isJumping) {
        event.getController().setAnimation(
            new AnimationBuilder()
                .addAnimation(Animations.INIT_JUMP));
      }

      return PlayState.CONTINUE;
    }

    if (isJumping && ((AnimationControllerExtended) event.getController()).isAnimationFinished()) {
      event.getController().setAnimation(
          new AnimationBuilder()
              .addAnimation(Animations.STOP_JUMP));

      return PlayState.CONTINUE;
    }

    return PlayState.CONTINUE;
  }

  @SuppressWarnings("rawtypes")
  private PlayState mouthPredicate(final AnimationEvent<? extends IAnimatable> event) {
    // if (this.ticksIdling < 5) {
    // return PlayState.STOP;
    // }

    if (this.isMouthOpen()) {
      event.getController().setAnimation(
          new AnimationBuilder()
              .addAnimation(Animations.OPEN_MOUTH)
              .addAnimation(Animations.IDLE_MOUTH, true));

      return PlayState.CONTINUE;
    }

    if (AnimationUtil.isCurrentAnimation(Animations.IDLE_MOUTH, event.getController())) {
      event.getController().setAnimation(
          new AnimationBuilder()
              .addAnimation(Animations.CLOSE_MOUTH));

      return PlayState.CONTINUE;
    }

    final AnimationController jumpController = AnimationUtil
        .getEntityController(this, Controllers.JUMP);

    final HashMap<String, String> hash = new HashMap<String, String>();

    hash.put(Animations.INIT_JUMP, Animations.INIT_JUMP_MOUTH);
    hash.put(Animations.JUMP, Animations.JUMP_MOUTH);
    hash.put(Animations.STOP_JUMP, Animations.STOP_JUMP_MOUTH);

    if (jumpController.getCurrentAnimation() != null
        && jumpController.getAnimationState() == AnimationState.Running) {
      event.getController().setAnimation(
          new AnimationBuilder()
              .addAnimation(
                  hash.get(jumpController.getCurrentAnimation().animationName),
                  jumpController.getCurrentAnimation().loop));

      return PlayState.CONTINUE;
    }

    return PlayState.CONTINUE;
  }
}
