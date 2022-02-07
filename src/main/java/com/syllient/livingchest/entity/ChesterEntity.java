package com.syllient.livingchest.entity;

import com.syllient.livingchest.GuiHandler;
import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.animation.ChesterAnimation;
import com.syllient.livingchest.inventory.ChesterInventory;
import com.syllient.livingchest.registry.SoundRegistry;
import com.syllient.livingchest.saveddata.ChesterSavedData;
import com.syllient.livingchest.util.InventoryUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class ChesterEntity extends EntityTameable implements IAnimatable {
  private static final DataParameter<Boolean> IS_MOUTH_OPEN =
      EntityDataManager.createKey(ChesterEntity.class, DataSerializers.BOOLEAN);
  private final ChesterInventory inventory = new ChesterInventory(this, 27);
  private final ChesterAnimation animation = new ChesterAnimation(this);
  private int ticksUntilCanMove = 0;

  public ChesterEntity(final World worldIn) {
    super(worldIn);
    this.setSize(1.0F, 1.0F);
    this.ignoreFrustumCheck = true;
  }

  @Override
  protected void initEntityAI() {
    this.aiSit = new EntityAISit(this);
    this.tasks.addTask(1, new EntityAISwimming(this));
    this.tasks.addTask(2, this.aiSit);
    this.tasks.addTask(3, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
    this.tasks.addTask(4, new EntityAIWanderAvoidWater(this, 1.0D));
  }

  @Override
  protected void entityInit() {
    super.entityInit();
    this.dataManager.register(IS_MOUTH_OPEN, false);
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    // TODO: Vida 450.0D
    this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1.0D);
    this.setMoveSpeed(this.getMoveSpeed());
    this.addPotionEffect(
        new PotionEffect(MobEffects.REGENERATION, Integer.MAX_VALUE, 2, false, false));
  }

  @Override
  public NBTTagCompound writeToNBT(final NBTTagCompound nbt) {
    ChesterSavedData.get(this.world).saveChesterPosition(this);
    return super.writeToNBT(this.writeToSavedChesterNbt(nbt));
  }

  @Override
  public void readFromNBT(final NBTTagCompound nbt) {
    this.readFromSavedChesterNbt(nbt);
    super.readFromNBT(nbt);
  }

  public NBTTagCompound writeToSavedChesterNbt(final NBTTagCompound nbt) {
    if (this.inventory != null) {
      nbt.setTag("Inventory", this.inventory.serializeNBT());
    }

    return nbt;
  }

  public void readFromSavedChesterNbt(final NBTTagCompound nbt) {
    if (nbt.hasKey("Inventory")) {
      this.inventory.deserializeNBT(nbt.getCompoundTag("Inventory"));
    }
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
    if (!this.isMouthOpen() && this.onGround && this.ticksUntilCanMove > 0
        && --this.ticksUntilCanMove == 0) {
      this.setMoveSpeed(this.getMoveSpeed());
    }
  }

  public void onClientUpdate() {}

  @Override
  public void onDeath(final DamageSource cause) {
    if (!this.world.isRemote) {
      InventoryUtil.dropInventoryItems(this.world, this, this.inventory);
      ChesterSavedData.get(this.world).onChesterDie(this);
    }

    super.onDeath(cause);
  }

  @Override
  public boolean processInteract(final EntityPlayer player, final EnumHand hand) {
    if (this.world.isRemote) {
      return true;
    }

    if (hand == EnumHand.MAIN_HAND) {
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
    this.ticksUntilCanMove = 15;
  }

  public void openGuiTo(final EntityPlayer player) {
    player.openGui(LivingChest.INSTANCE, GuiHandler.Gui.CHESTER, this.world, this.getEntityId(), 0,
        0);
  }

  public double getMoveSpeed() {
    return 0.375D;
  }

  public void setMoveSpeed(final double value) {
    this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(value);
  }

  @Override
  protected SoundEvent getDeathSound() {
    return SoundRegistry.Entity.Chester.DEATH;
  }

  @Override
  protected SoundEvent getHurtSound(final DamageSource damageSourceIn) {
    return SoundRegistry.Entity.Chester.HURT;
  }

  @Override
  public void playLivingSound() {
    // Handled by Animation class
  }

  @Override
  protected void playStepSound(final BlockPos pos, final Block blockIn) {
    // Handled by Animation class
  }

  @Override
  public EntityAgeable createChild(final EntityAgeable ageable) {
    return null;
  }

  public int getDeathCooldown() {
    // final int minutes = 1; // TODO: Death Cooldown
    return 500; // minutes * 20 * 60
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
    return this.animation.getFactory();
  }

  @Override
  public void registerControllers(final AnimationData data) {
    this.animation.registerControllers(data);
  }
}
