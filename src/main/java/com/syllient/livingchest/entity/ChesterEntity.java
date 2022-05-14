package com.syllient.livingchest.entity;

import java.util.stream.IntStream;
import com.syllient.livingchest.animation.animator.entity.ChesterAnimator;
import com.syllient.livingchest.container.ChesterContainer;
import com.syllient.livingchest.entity.ai.ChesterSitAi;
import com.syllient.livingchest.entity.ai.helper.ChesterMoveHelper;
import com.syllient.livingchest.entity.ai.pathfinding.FixedGroundPathNavigator;
import com.syllient.livingchest.eventhandler.registry.BlockRegistry;
import com.syllient.livingchest.eventhandler.registry.ItemRegistry;
import com.syllient.livingchest.eventhandler.registry.SoundRegistry;
import com.syllient.livingchest.inventory.ChesterInventory;
import com.syllient.livingchest.saveddata.VirtualChesterSavedData;
import com.syllient.livingchest.util.InventoryUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class ChesterEntity extends TameableEntity
    implements IAnimatable, IEntityAdditionalSpawnData, INamedContainerProvider {
  private static final DataParameter<Boolean> IS_MOUTH_OPEN =
      EntityDataManager.defineId(ChesterEntity.class, DataSerializers.BOOLEAN);

  private static final DataParameter<Boolean> IS_MOVING =
      EntityDataManager.defineId(ChesterEntity.class, DataSerializers.BOOLEAN);

  private static final DataParameter<Boolean> IS_DESPAWNING =
      EntityDataManager.defineId(ChesterEntity.class, DataSerializers.BOOLEAN);

  private final ChesterInventory inventory = new ChesterInventory(this, 27);

  private final ChesterAnimator animator = new ChesterAnimator(this);

  private BlockPos eyeBone;

  private int ticksUntilActionEnd = 25; // To spawn animation

  private int ticksDead;

  private int ticksDespawning;

  public ChesterEntity(final EntityType<? extends ChesterEntity> type, final World world) {
    super(type, world);
    this.addEffect(new EffectInstance(Effects.REGENERATION, Integer.MAX_VALUE, 1, false, false));
    this.moveControl = new ChesterMoveHelper(this);
    this.noCulling = true;
  }

  @Override
  protected PathNavigator createNavigation(final World world) {
    return new FixedGroundPathNavigator(this, world);
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.entityData.define(IS_MOUTH_OPEN, false);
    this.entityData.define(IS_MOVING, false);
    this.entityData.define(IS_DESPAWNING, false);
  }

  @Override
  protected void registerGoals() {
    this.goalSelector.addGoal(1, new SwimGoal(this));
    this.goalSelector.addGoal(2, new ChesterSitAi(this));
    this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
  }

  @Override
  public void addAdditionalSaveData(final CompoundNBT compoundIn) {
    VirtualChesterSavedData.getServerInstance(this.level).handleChesterDataSave(this);

    if (this.inventory != null) {
      compoundIn.put(NbtKey.INVENTORY, this.inventory.serializeNBT());
    }

    if (this.eyeBone != null) {
      compoundIn.put(NbtKey.EYE_BONE, NBTUtil.writeBlockPos(this.eyeBone));
    }

    super.addAdditionalSaveData(compoundIn);
  }

  @Override
  public void readAdditionalSaveData(final CompoundNBT compoundIn) {
    if (compoundIn.contains(NbtKey.INVENTORY)) {
      this.inventory.deserializeNBT(compoundIn.getCompound(NbtKey.INVENTORY));
    }

    if (compoundIn.contains(NbtKey.EYE_BONE)) {
      this.eyeBone = NBTUtil.readBlockPos(compoundIn.getCompound(NbtKey.EYE_BONE));
    }

    super.readAdditionalSaveData(compoundIn);
  }

  @Override
  public void writeSpawnData(final PacketBuffer buffer) {
    buffer.writeFloat(this.yRot);
  }

  @Override
  public void readSpawnData(final PacketBuffer buffer) {
    this.setYawRotations(buffer.readFloat());
    this.setPrevYawRotations(this.yRot);
  }

  @Override
  public IPacket<?> getAddEntityPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  @Override
  public void tick() {
    super.tick();

    if (this.level.isClientSide) {
      this.tickClient();
    } else {
      this.tickServer();
    }
  }

  private void tickClient() {}

  private void tickServer() {
    if (this.ticksUntilActionEnd > 0) {
      this.ticksUntilActionEnd--;
    }

    if (this.isDespawning()) {
      if (++this.ticksDespawning >= 50) {
        this.resetActionTicks();
        this.despawn();
        return;
      }

      this.setTicksUntilActionEnd(Integer.MAX_VALUE);
    } else if (this.isInSittingPose()) {
      this.setTicksUntilActionEnd(45);
    } else if (this.isMouthOpen()) {
      this.setTicksUntilActionEnd(10);
    }

    if (this.tickCount % 40 == 0) {
      this.checkEyeBone();
    }
  }

  private void checkEyeBone() {
    if (this.eyeBone == null) {
      this.setInSittingPose(false);

      if (this.isTame()) {
        final PlayerEntity owner = (PlayerEntity) this.getOwner();

        final boolean shouldDespawn = owner == null || !(owner.containerMenu.getItems().stream()
            .map(ItemStack::getItem).anyMatch(ItemRegistry.EYE_BONE::equals));

        if (shouldDespawn) {
          this.despawn();
        }
      }
    } else {
      this.setInSittingPose(true);

      final boolean isEyeBoneBlock =
          this.level.getBlockState(this.eyeBone).getBlock().equals(BlockRegistry.EYE_BONE);

      if (!isEyeBoneBlock) {
        this.eyeBone = null;
      }
    }
  }

  @Override
  public void die(final DamageSource source) {
    if (!this.level.isClientSide) {
      VirtualChesterSavedData.getServerInstance(this.level).handleChesterDeath(this);
      InventoryUtil.dropItems(this.level, this, this.inventory);
    }

    super.die(source);
  }

  @Override
  protected void tickDeath() {
    if (++this.ticksDead == 60) {
      this.remove();

      IntStream.range(0, 20).forEach((i) -> {
        final double d0 = this.random.nextGaussian() * 0.02D;
        final double d1 = this.random.nextGaussian() * 0.02D;
        final double d2 = this.random.nextGaussian() * 0.02D;
        this.level.addParticle(ParticleTypes.POOF, this.getRandomX(1.0D), this.getRandomY(),
            this.getRandomZ(1.0D), d0, d1, d2);
      });
    }
  }

  @Override
  public ActionResultType interactAt(final PlayerEntity player, final Vector3d vector,
      final Hand hand) {
    if (this.level.isClientSide) {
      return ActionResultType.SUCCESS;
    }

    if (hand == Hand.MAIN_HAND) {
      this.openGuiTo(player);
      return ActionResultType.SUCCESS;
    }

    return ActionResultType.PASS;
  }

  public boolean isDespawning() {
    return this.entityData.get(IS_DESPAWNING);
  }

  public void setIsDespawning() {
    this.setIsDespawning(true);
  }

  private void setIsDespawning(final boolean value) {
    this.entityData.set(IS_DESPAWNING, value);
  }

  public boolean isMouthOpen() {
    return this.entityData.get(IS_MOUTH_OPEN);
  }

  private void setIsMouthOpen(final boolean value) {
    this.entityData.set(IS_MOUTH_OPEN, value);
  }

  public boolean isMoving() {
    return this.entityData.get(IS_MOVING);
  }

  public void setIsMoving(final boolean value) {
    this.entityData.set(IS_MOVING, value);
  }

  public void openMouth() {
    this.setIsMouthOpen(true);
  }

  public void closeMouth() {
    this.setIsMouthOpen(false);
  }

  public void openGuiTo(final PlayerEntity player) {
    NetworkHooks.openGui((ServerPlayerEntity) player, this, (buf) -> {
      buf.writeInt(this.getId());
    });
  }

  private void despawn() {
    VirtualChesterSavedData.getServerInstance(this.level).despawnChester(this);
  }

  @Override
  public Container createMenu(final int windowId, final PlayerInventory inventory,
      final PlayerEntity player) {
    return new ChesterContainer(windowId, player, this);
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
  protected void playStepSound(final BlockPos pos, final BlockState state) {}

  @Override
  public AgeableEntity getBreedOffspring(final ServerWorld world, final AgeableEntity entity) {
    return null;
  }

  public void setYawRotations(final float yaw) {
    this.yRot = yaw;
    this.yHeadRot = yaw;
    this.yBodyRot = yaw;
  }

  public void setPrevYawRotations(final float yaw) {
    this.yRotO = yaw;
    this.yHeadRotO = yaw;
    this.yBodyRotO = yaw;
  }

  public int getTicksUntilActionEnd() {
    return this.ticksUntilActionEnd;
  }

  public void setTicksUntilActionEnd(final int ticks) {
    if (ticks < this.ticksUntilActionEnd) {
      return;
    }

    this.ticksUntilActionEnd = ticks;
  }

  public void resetActionTicks() {
    this.ticksUntilActionEnd = 0;
  }

  public int getTicksDespawning() {
    return this.ticksDespawning;
  }

  public int getDeathCooldown() {
    // final int minutes = 10;
    // return minutes * 20 * 60;
    return 0; // TODO: 10 minutes
  }

  @Override
  protected float getJumpPower() {
    return 0.5F;
  }

  public ChesterInventory getInventory() {
    return this.inventory;
  }

  @Override
  public AnimationFactory getFactory() {
    return this.animator.getFactory();
  }

  @Override
  public void registerControllers(final AnimationData data) {
    this.animator.registerControllers(data);
  }

  class NbtKey {
    public static final String INVENTORY = "Inventory";
    public static final String EYE_BONE = "EyeBone";
  }
}
