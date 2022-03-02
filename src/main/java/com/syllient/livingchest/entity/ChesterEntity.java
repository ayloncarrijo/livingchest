package com.syllient.livingchest.entity;

import com.syllient.livingchest.animation.entity.ChesterAnimation;
import com.syllient.livingchest.entity.ai.helper.ChesterMoveHelper;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class ChesterEntity extends TameableEntity implements IAnimatable {
  private static final DataParameter<Boolean> IS_MOUTH_OPEN =
      EntityDataManager.defineId(ChesterEntity.class, DataSerializers.BOOLEAN);
  private final ChesterAnimation animation = new ChesterAnimation(this);
  private BlockPos eyeBone;

  public ChesterEntity(final EntityType<? extends ChesterEntity> entityType, final World world) {
    super(entityType, world);
    this.addEffect(new EffectInstance(Effects.REGENERATION, Integer.MAX_VALUE, 1, false, false));
    this.moveControl = new ChesterMoveHelper(this);
    this.noCulling = true;
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.entityData.define(IS_MOUTH_OPEN, false);
  }

  @Override
  protected void registerGoals() {
    this.goalSelector.addGoal(1, new SwimGoal(this));
    this.goalSelector.addGoal(2, new SitGoal(this));
    this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
  }

  public boolean isMouthOpen() {
    return this.entityData.get(IS_MOUTH_OPEN);
  }

  private void setIsMouthOpen(final boolean value) {
    this.entityData.set(IS_MOUTH_OPEN, value);
  }

  public void openMouth() {
    this.setIsMouthOpen(true);
  }

  public void closeMouth() {
    this.setIsMouthOpen(false);
  }

  public BlockPos getEyeBone() {
    return this.eyeBone;
  }

  public void setEyeBone(final BlockPos pos) {
    this.eyeBone = pos;
  }

  @Override
  public AgeableEntity getBreedOffspring(final ServerWorld world, final AgeableEntity entity) {
    return null;
  }

  @Override
  public void registerControllers(final AnimationData data) {

  }

  @Override
  public AnimationFactory getFactory() {
    return new AnimationFactory(this);
  }
}
