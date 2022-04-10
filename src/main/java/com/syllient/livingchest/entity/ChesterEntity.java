package com.syllient.livingchest.entity;

import com.syllient.livingchest.GuiHandler;
import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.animation.entity.ChesterAnimation;
import com.syllient.livingchest.entity.ai.ChesterSitAi;
import com.syllient.livingchest.entity.ai.helper.ChesterMoveHelper;
import com.syllient.livingchest.eventhandler.registry.BlockRegistry;
import com.syllient.livingchest.eventhandler.registry.ItemRegistry;
import com.syllient.livingchest.eventhandler.registry.SoundRegistry;
import com.syllient.livingchest.inventory.ChesterInventory;
import com.syllient.livingchest.saveddata.VirtualChesterSavedData;
import com.syllient.livingchest.util.InventoryUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAISwimming;
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
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class ChesterEntity extends EntityTameable
    implements IAnimatable, IEntityAdditionalSpawnData {
  private static final DataParameter<Boolean> IS_MOUTH_OPEN =
      EntityDataManager.createKey(ChesterEntity.class, DataSerializers.BOOLEAN);
  private static final DataParameter<Boolean> IS_MOVING =
      EntityDataManager.createKey(ChesterEntity.class, DataSerializers.BOOLEAN);

  private final ChesterInventory inventory = new ChesterInventory(this, 27);
  private final ChesterAnimation animation = new ChesterAnimation(this);
  private BlockPos eyeBone;

  public ChesterEntity(final World worldIn) {
    super(worldIn);
    this.setSize(0.7F, 0.85F);
    this.addPotionEffect(
        new PotionEffect(MobEffects.REGENERATION, Integer.MAX_VALUE, 1, false, false));
    this.moveHelper = new ChesterMoveHelper(this);
    this.ignoreFrustumCheck = true;
  }

  @Override
  protected void entityInit() {
    super.entityInit();
    this.dataManager.register(IS_MOUTH_OPEN, false);
    this.dataManager.register(IS_MOVING, false);
  }

  @Override
  protected void initEntityAI() {
    this.aiSit = new ChesterSitAi(this);
    this.tasks.addTask(1, new EntityAISwimming(this));
    this.tasks.addTask(2, this.aiSit);
    this.tasks.addTask(3, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1.0D); // TODO
    this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.37D);
  }

  @Override
  public NBTTagCompound writeToNBT(final NBTTagCompound compoundIn) {
    VirtualChesterSavedData.getServerInstance(this.world).handleChesterDataSave(this);

    if (this.inventory != null) {
      compoundIn.setTag(NbtKey.INVENTORY, this.inventory.serializeNBT());
    }

    if (this.eyeBone != null) {
      compoundIn.setTag(NbtKey.EYE_BONE, NBTUtil.createPosTag(this.eyeBone));
    }

    return super.writeToNBT(compoundIn);
  }

  @Override
  public void readFromNBT(final NBTTagCompound compoundIn) {
    if (compoundIn.hasKey(NbtKey.INVENTORY)) {
      this.inventory.deserializeNBT(compoundIn.getCompoundTag(NbtKey.INVENTORY));
    }

    if (compoundIn.hasKey(NbtKey.EYE_BONE)) {
      this.eyeBone = NBTUtil.getPosFromTag(compoundIn.getCompoundTag(NbtKey.EYE_BONE));
    }

    super.readFromNBT(compoundIn);
  }

  @Override
  public void writeSpawnData(final ByteBuf buffer) {
    buffer.writeFloat(this.rotationYaw);
  }

  @Override
  public void readSpawnData(final ByteBuf buffer) {
    this.setYawRotations(buffer.readFloat());
    this.setPrevYawRotations(this.rotationYaw);
  }

  @Override
  public void onUpdate() {
    super.onUpdate();

    if (this.world.isRemote) {
      this.handleClientTick();
    } else {
      this.handleServerTick();
    }
  }

  private void handleClientTick() {}

  private void handleServerTick() {
    this.checkEyeBone();
  }

  private void checkEyeBone() {
    if (this.eyeBone == null) {
      this.setSitting(false);

      if (this.isTamed() && ticksExisted % 60 == 0) {
        final EntityPlayer owner = (EntityPlayer) this.getOwner();

        final boolean shouldDespawn =
            owner == null || !(owner.inventoryContainer.inventoryItemStacks.stream()
                .map(ItemStack::getItem).anyMatch(ItemRegistry.EYE_BONE::equals));

        if (shouldDespawn) {
          VirtualChesterSavedData.getServerInstance(this.world).despawnChester(this);
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
  public void onDeath(final DamageSource source) {
    if (!this.world.isRemote) {
      VirtualChesterSavedData.getServerInstance(this.world).handleChesterDeath(this);
      InventoryUtil.dropInventoryItems(this.world, this, this.inventory);
    }

    super.onDeath(source);
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

  public boolean isMoving() {
    return this.dataManager.get(IS_MOVING);
  }

  public void setIsMoving(final boolean value) {
    this.dataManager.set(IS_MOVING, value);
  }

  public boolean isMouthOpen() {
    return this.dataManager.get(IS_MOUTH_OPEN);
  }

  private void setIsMouthOpen(final boolean value) {
    this.dataManager.set(IS_MOUTH_OPEN, value);
  }

  public void openMouth() {
    this.setIsMouthOpen(true);
  }

  public void closeMouth() {
    this.setIsMouthOpen(false);
  }

  public void openGuiTo(final EntityPlayer player) {
    player.openGui(LivingChest.INSTANCE, GuiHandler.Gui.CHESTER, this.world, this.getEntityId(), 0,
        0);
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
  protected SoundEvent getHurtSound(final DamageSource source) {
    return SoundRegistry.Entity.Chester.HURT;
  }

  @Override
  protected void playStepSound(final BlockPos pos, final Block blockIn) {}

  @Override
  public EntityAgeable createChild(final EntityAgeable ageable) {
    return null;
  }

  public void setYawRotations(final float yaw) {
    this.rotationYaw = yaw;
    this.rotationYawHead = yaw;
    this.renderYawOffset = yaw;
  }

  public void setPrevYawRotations(final float yaw) {
    this.prevRotationYaw = yaw;
    this.prevRotationYawHead = yaw;
    this.prevRenderYawOffset = yaw;
  }

  public int getDeathCooldown() {
    // TODO
    // final int minutes = 10;
    // return minutes * 20 * 60;
    return 1200;
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

  class NbtKey {
    public static final String INVENTORY = "Inventory";
    public static final String EYE_BONE = "EyeBone";
  }
}
