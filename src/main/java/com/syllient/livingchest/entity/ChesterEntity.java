package com.syllient.livingchest.entity;

import com.syllient.livingchest.GuiHandler;
import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.animation.ChesterAnimation;
import com.syllient.livingchest.entity.ai.ChesterSitEntityAi;
import com.syllient.livingchest.inventory.ChesterInventory;
import com.syllient.livingchest.registry.BlockRegistry;
import com.syllient.livingchest.registry.ItemRegistry;
import com.syllient.livingchest.registry.SoundRegistry;
import com.syllient.livingchest.saveddata.VirtualChesterSavedData;
import com.syllient.livingchest.util.InventoryUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
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
  private BlockPos eyeBone;
  private int ticksUntilCanMove = 0;

  public ChesterEntity(final World worldIn) {
    super(worldIn);
    this.setSize(1.0F, 1.0F);
    this.ignoreFrustumCheck = true;
  }

  @Override
  protected void initEntityAI() {
    this.aiSit = new ChesterSitEntityAi(this);
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
    this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(450.0D);
    this.setMoveSpeed(this.getMoveSpeed());
    this.addPotionEffect(
        new PotionEffect(MobEffects.REGENERATION, Integer.MAX_VALUE, 1, false, false));
  }

  @Override
  public NBTTagCompound writeToNBT(final NBTTagCompound tagCompoundIn) {
    VirtualChesterSavedData.getInstance(this.world).onChesterWriteToNbt(this);

    if (this.inventory != null) {
      tagCompoundIn.setTag(TagKey.INVENTORY, this.inventory.serializeNBT());
    }

    if (this.eyeBone != null) {
      tagCompoundIn.setTag(TagKey.EYE_BONE, NBTUtil.createPosTag(this.eyeBone));
    }

    return super.writeToNBT(tagCompoundIn);
  }

  @Override
  public void readFromNBT(final NBTTagCompound tagCompoundIn) {
    if (tagCompoundIn.hasKey(TagKey.INVENTORY)) {
      this.inventory.deserializeNBT(tagCompoundIn.getCompoundTag(TagKey.INVENTORY));
    }

    if (tagCompoundIn.hasKey(TagKey.EYE_BONE)) {
      this.eyeBone = NBTUtil.getPosFromTag(tagCompoundIn.getCompoundTag(TagKey.EYE_BONE));
    }

    super.readFromNBT(tagCompoundIn);
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

    this.checkEyeBone();
  }

  public void onClientUpdate() {}

  private void checkEyeBone() {
    if (this.eyeBone == null) {
      this.setSitting(false);

      if (this.getOwnerId() != null && ticksExisted % 60 == 0) {
        final EntityPlayer owner = this.world.getPlayerEntityByUUID(this.getOwnerId());

        final boolean shouldDespawn =
            owner == null || !(owner.inventoryContainer.inventoryItemStacks.stream()
                .map(ItemStack::getItem).anyMatch(ItemRegistry.EYE_BONE::equals));

        if (shouldDespawn) {
          VirtualChesterSavedData.getInstance(this.world).despawnChester(this.getOwnerId(),
              this.world);
        }
      }
    } else {
      this.setSitting(true);

      final boolean isEyeBoneBlock =
          this.world.getBlockState(this.eyeBone).getBlock().equals(BlockRegistry.EYE_BONE);

      if (!isEyeBoneBlock) {
        this.eyeBone = null;
      }
    }
  }

  @Override
  public void onDeath(final DamageSource cause) {
    if (!this.world.isRemote) {
      VirtualChesterSavedData.getInstance(this.world).onChesterDie(this);
      InventoryUtil.dropInventoryItems(this.world, this, this.inventory);
    }

    super.onDeath(cause);
  }

  @Override
  public void setDead() {
    if (!this.world.isRemote) {
      VirtualChesterSavedData.getInstance(this.world).onChesterSetDead(this);
    }

    super.setDead();
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
    return 0.370D;
  }

  public void setMoveSpeed(final double value) {
    this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(value);
  }

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
  protected SoundEvent getHurtSound(final DamageSource damageSourceIn) {
    return SoundRegistry.Entity.Chester.HURT;
  }

  @Override
  public void playLivingSound() {}

  @Override
  protected void playStepSound(final BlockPos pos, final Block blockIn) {}

  @Override
  public EntityAgeable createChild(final EntityAgeable ageable) {
    return null;
  }

  public int getDeathCooldown() {
    final int minutes = 10;
    return minutes * 20 * 60;
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

  class TagKey {
    public static final String INVENTORY = "Inventory";
    public static final String EYE_BONE = "EyeBone";
  }
}
