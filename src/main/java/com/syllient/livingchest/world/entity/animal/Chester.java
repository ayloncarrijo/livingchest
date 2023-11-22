package com.syllient.livingchest.world.entity.animal;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class Chester extends TameableEntity implements IAnimatable {
  private final AnimationFactory animationFactory = GeckoLibUtil.createFactory(this);

  public Chester(final EntityType<? extends Chester> entityType, final World level) {
    super(entityType, level);
  }

  public static AttributeModifierMap.MutableAttribute createAttributes() {
    return MobEntity.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3D)
        .add(Attributes.MAX_HEALTH, 8.0D);
  }

  @Override
  public AgeableEntity getBreedOffspring(final ServerWorld level, final AgeableEntity mob) {
    return null;
  }

  @Override
  public void registerControllers(final AnimationData data) {

  }

  @Override
  public AnimationFactory getFactory() {
    return this.animationFactory;
  }
}
