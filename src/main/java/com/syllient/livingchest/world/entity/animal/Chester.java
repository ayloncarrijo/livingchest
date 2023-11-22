package com.syllient.livingchest.world.entity.animal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class Chester extends TamableAnimal implements IAnimatable {
  private final AnimationFactory animationFactory = GeckoLibUtil.createFactory(this);

  public Chester(final EntityType<? extends Chester> entityType, final Level level) {
    super(entityType, level);
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3D).add(Attributes.MAX_HEALTH,
        8.0D);
  }

  @Override
  public AgeableMob getBreedOffspring(final ServerLevel level, final AgeableMob mob) {
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
