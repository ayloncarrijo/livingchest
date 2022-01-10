package com.syllient.livingchest.entity;

import com.syllient.livingchest.GuiHandler;
import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.animation.ChesterAnimation;
import com.syllient.livingchest.inventory.ChesterInventory;
import com.syllient.livingchest.registry.SoundRegistry;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class ChesterEntity extends EntityCow implements IAnimatable {
  private static final DataParameter<Boolean> IS_MOUTH_OPEN = EntityDataManager.createKey(ChesterEntity.class,
      DataSerializers.BOOLEAN);

  private final ChesterInventory inventory = new ChesterInventory(this, 27);
  private final AnimationFactory factory = new AnimationFactory(this);
  private final ChesterAnimation animation = new ChesterAnimation(this);
  private int ticksUntilResetMoveSpeed = 0;

  public ChesterEntity(final World worldIn) {
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
    this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
        .setBaseValue(this.getDefaultMoveSpeed());
    this.addPotionEffect(
        new PotionEffect(
            MobEffects.REGENERATION,
            Integer.MAX_VALUE,
            2,
            false,
            false));
  }

  @Override
  public void onUpdate() {
    super.onUpdate();

    if (!this.world.isRemote) {
      this.onServerUpdate();
    } else {
      this.onClientUpdate();
    }
  }

  public void onServerUpdate() {
    if (!this.isMouthOpen() && this.onGround
        && this.ticksUntilResetMoveSpeed > 0
        && --this.ticksUntilResetMoveSpeed <= 0) {
      this.setMoveSpeed(this.getDefaultMoveSpeed());
    }
  }

  public void onClientUpdate() {
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
    this.playSound(SoundRegistry.Chester.OPEN_MOUTH, 1.0F, 1.0F);
  }

  public void closeMouth() {
    this.setIsMouthOpen(false);
    this.ticksUntilResetMoveSpeed = 15;
    this.playSound(SoundRegistry.Chester.CLOSE_MOUTH, 1.0F, 1.0F);
  }

  public void openGuiTo(final EntityPlayer player) {
    player.openGui(
        LivingChest.instance,
        GuiHandler.Gui.CHESTER,
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

  public ChesterInventory getInventory() {
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
