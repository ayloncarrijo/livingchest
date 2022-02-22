package com.syllient.livingchest.entity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
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

  public static EntityType<ChesterEntity> buildRegistry() {
    final String name = "chester";
    final EntityType<ChesterEntity> entityType = EntityType.Builder
        .of(ChesterEntity::new, EntityClassification.CREATURE).sized(0.7F, 0.85F).build(name);

    entityType.setRegistryName(name);

    return entityType;
  }

  public static AttributeModifierMap buildAttributes() {
    return MobEntity.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3D)
        .add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.ATTACK_DAMAGE, 2.0D).build();
  }
}
