package com.syllient.livingchest.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class ChesterEntity extends WolfEntity implements IAnimatable, IAnimationTickable {
  // protected ChesterEntity(final EntityType<? extends TameableEntity> entityType, final World
  // world) {
  // super(entityType, world);
  // }

  // @Override
  // public AgeableEntity getBreedOffspring(final ServerWorld world, final AgeableEntity entity) {
  // return null;
  // }

  public ChesterEntity(final EntityType<? extends WolfEntity> entityType, final World world) {
    super(entityType, world);
  }

  @Override
  public void registerControllers(final AnimationData data) {

  }

  @Override
  public AnimationFactory getFactory() {
    return new AnimationFactory(this);
  }

  @Override
  public int tickTimer() {
    return this.tickCount;
  }
}
