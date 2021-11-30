package com.syllient.livingchest.entity;

import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class EntityChester extends EntityCow implements IAnimatable {
  private AnimationFactory factory = new AnimationFactory(this);

  public EntityChester(World worldIn) {
    super(worldIn);
    this.ignoreFrustumCheck = true;
  }

  @Override
  public void registerControllers(AnimationData data) {

  }

  @Override
  public AnimationFactory getFactory() {
    return this.factory;
  }

  @Override
  public boolean processInteract(EntityPlayer player, EnumHand hand) {
    if (hand == EnumHand.MAIN_HAND) {
      return true;
    } else {
      return super.processInteract(player, hand);
    }
  }
}
