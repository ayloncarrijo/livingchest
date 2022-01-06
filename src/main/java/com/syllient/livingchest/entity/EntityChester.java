package com.syllient.livingchest.entity;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.ModGuiHandler;
import com.syllient.livingchest.animation.entity.AnimationChester;
import com.syllient.livingchest.inventory.InventoryChester;

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
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class EntityChester extends EntityCow implements IAnimatable {
  private static final DataParameter<Boolean> IS_MOUTH_OPEN = EntityDataManager.createKey(EntityChester.class,
      DataSerializers.BOOLEAN);

  private final InventoryChester inventory = new InventoryChester(this, 27);
  private final AnimationFactory factory = new AnimationFactory(this);
  private final AnimationChester animation = new AnimationChester(this);

  private int ticksUntilResetMoveSpeed = 0;

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
  public void onUpdate() {
    if (!this.world.isRemote) {
      if (!this.isMouthOpen()
          && this.ticksUntilResetMoveSpeed > 0
          && --this.ticksUntilResetMoveSpeed <= 0) {
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
    this.ticksUntilResetMoveSpeed = 10;
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

  public InventoryChester getInventory() {
    return this.inventory;
  }

  @Override
  public AnimationFactory getFactory() {
    return this.factory;
  }

  @Override
  public void registerControllers(final AnimationData data) {
    this.animation.registerControllers(data);
  }
}
