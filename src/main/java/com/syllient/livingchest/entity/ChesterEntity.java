package com.syllient.livingchest.entity;

import com.syllient.livingchest.animation.entity.ChesterAnimation;
import com.syllient.livingchest.entity.ai.ChesterSitAi;
import com.syllient.livingchest.entity.ai.helper.ChesterMoveHelper;
import com.syllient.livingchest.eventhandler.registry.SoundRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class ChesterEntity extends TameableEntity
    implements IAnimatable, IEntityAdditionalSpawnData {
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
    this.goalSelector.addGoal(2, new ChesterSitAi(this));
    this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
  }

  @Override
  public void addAdditionalSaveData(final CompoundNBT compoundNbt) {
    super.addAdditionalSaveData(compoundNbt);
  }

  @Override
  public void readAdditionalSaveData(final CompoundNBT compoundNbt) {
    super.readAdditionalSaveData(compoundNbt);
  }

  @Override
  public void writeSpawnData(final PacketBuffer buffer) {}

  @Override
  public void readSpawnData(final PacketBuffer additionalData) {}

  @Override
  public void tick() {
    super.tick();

    if (this.level.isClientSide) {
      this.onServerUpdate();
    } else {
      this.onClientUpdate();
    }
  }

  private void onServerUpdate() {
    this.checkEyeBone();
  }

  private void onClientUpdate() {}

  private void checkEyeBone() {}

  @Override
  public void die(final DamageSource source) {
    super.die(source);
  }

  @Override
  public void remove(final boolean shouldKeepData) {
    super.remove(shouldKeepData);
  }

  @Override
  public ActionResultType interactAt(final PlayerEntity player, final Vector3d vector,
      final Hand hand) {
    return super.interactAt(player, vector, hand);
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

  public void openGuiTo(final PlayerEntity player) {}

  public BlockPos getEyeBone() {
    return this.eyeBone;
  }

  public void setEyeBone(final BlockPos pos) {
    this.eyeBone = pos;
  }

  @Override
  protected SoundEvent getDeathSound() {
    return SoundRegistry.Entity.Chester.DEATH;
  }

  @Override
  protected SoundEvent getHurtSound(final DamageSource source) {
    return SoundRegistry.Entity.Chester.HURT;
  }

  @Override
  protected void playStepSound(final BlockPos pos, final BlockState state) {}

  @Override
  public AgeableEntity getBreedOffspring(final ServerWorld world, final AgeableEntity entity) {
    return null;
  }

  public void setYawRotation(final float yaw) {
    this.yRot = yaw;
    this.yHeadRot = yaw;
    this.yBodyRot = yaw;
  }

  public void setPrevYawRotation(final float yaw) {
    this.yRotO = yaw;
    this.yHeadRotO = yaw;
    this.yBodyRotO = yaw;
  }

  public int getDeathCooldown() {
    final int minutes = 10;
    return minutes * 20 * 60;
  }

  @Override
  protected float getJumpPower() {
    return 0.5F;
  }

  public void getInventory() {}

  @Override
  public AnimationFactory getFactory() {
    return this.animation.getFactory();
  }

  @Override
  public void registerControllers(final AnimationData data) {
    this.animation.registerControllers(data);
  }

  class NbtKey {
    public static final String INVENTORY = "Inventory";
    public static final String EYE_BONE = "EyeBone";
  }
}
