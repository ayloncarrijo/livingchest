package com.syllient.livingchest.entity;

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
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class EntityChester extends EntityCow implements IAnimatable {
  private static final DataParameter<Boolean> IS_MOUTH_OPEN = EntityDataManager.<Boolean>createKey(EntityChester.class,
      DataSerializers.BOOLEAN);

  private AnimationFactory factory = new AnimationFactory(this);

  public EntityChester(World worldIn) {
    super(worldIn);
    this.ignoreFrustumCheck = true;
  }

  private <E extends IAnimatable> PlayState mouthPredicate(AnimationEvent<E> event) {
    if (this.isMouthOpen()) {
      event.getController().setAnimation(
          new AnimationBuilder().addAnimation("animation.chester.open_mouth"));

      if (event.getController().getAnimationState() == AnimationState.Stopped) {
        event.getController().setAnimation(
            new AnimationBuilder().addAnimation("animation.chester.close_mouth"));
      }
    }

    // event.getController().setAnimation(
    // new AnimationBuilder().addAnimation(
    // this.isMouthOpen() ? "animation.chester.open_mouth" :
    // "animation.chester.close_mouth"));

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

  public void openMouth() {
    this.dataManager.set(IS_MOUTH_OPEN, true);
  }

  public void closeMouth() {
    this.dataManager.set(IS_MOUTH_OPEN, false);
  }

  public void toggleMouth() {
    this.dataManager.set(IS_MOUTH_OPEN, !this.isMouthOpen());
  }

  @Override
  public void onEntityUpdate() {
    super.onEntityUpdate();
  }

  @Override
  public boolean processInteract(EntityPlayer player, EnumHand hand) {
    if (!player.world.isRemote && hand == EnumHand.MAIN_HAND) {
      this.toggleMouth();

      return true;
    } else {
      return false;
    }
  }
}
