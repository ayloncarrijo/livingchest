package com.syllient.livingchest.saveddata;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.PacketHandler;
import com.syllient.livingchest.entity.ChesterEntity;
import com.syllient.livingchest.eventhandler.registry.EntityRegistry;
import com.syllient.livingchest.network.message.SyncVirtualChesterMessage;
import com.syllient.livingchest.util.NbtUtil;
import com.syllient.livingchest.util.Position;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.network.PacketDistributor;

public class VirtualChesterSavedData extends WorldSavedData {
  private static final String ID = LivingChest.MOD_ID + "_" + "virtualchester";
  private static final VirtualChesterSavedData INSTANCE = new VirtualChesterSavedData();
  private static final int TICK_DEAD_TIME_STEP = 600;

  private final Map<UUID, VirtualChester> virtualChesterByPlayerId = new HashMap<>();
  private int tickCount = 0;

  public VirtualChesterSavedData() {
    super(ID);
  }

  public VirtualChesterSavedData(final String id) {
    super(id);
  }

  public static VirtualChesterSavedData getServerInstance(final World world) {
    if (world.isClientSide) {
      throw new IllegalArgumentException("The world must be an instance of ServerWorld.");
    }

    return ((ServerWorld) world).getServer().overworld().getChunkSource().getDataStorage()
        .computeIfAbsent(VirtualChesterSavedData::new, ID);
  }

  public static VirtualChesterSavedData getClientInstance(final World world) {
    if (!world.isClientSide) {
      throw new IllegalArgumentException("The world must be an instance of ClientWorld.");
    }

    return INSTANCE;
  }

  public VirtualChester getVirtualChester(final UUID playerId) {
    return this.virtualChesterByPlayerId.computeIfAbsent(playerId, (key) -> new VirtualChester());
  }

  public void spawnChester(final PlayerEntity player, final BlockPos pos) {
    if (player.level.isClientSide) {
      return;
    }

    final VirtualChester virtualChester = this.getVirtualChester(player.getUUID());
    final ServerWorld world = (ServerWorld) player.level;

    if (virtualChester.isSpawned()) {
      return;
    }

    if (virtualChester.isDead()) {
      final int minutes = (int) Math.ceil((float) virtualChester.getDeadTime() / 20 / 60);

      player.sendMessage(new StringTextComponent(new StringBuilder()
          .append("Ooh, looks like your Chester is dead. You still need to wait ")
          .append(TextFormatting.RED).append(minutes).append(" minute")
          .append(minutes > 1 ? "s " : " ").append(TextFormatting.WHITE)
          .append("before you can summon him again.").toString()), Util.NIL_UUID);

      return;
    }

    if (!world.isEmptyBlock(pos.above())) {
      return;
    }

    final ChesterEntity chesterEntity = EntityRegistry.CHESTER.create(world);

    if (virtualChester.getAdditionalSaveData() != null) {
      chesterEntity.readAdditionalSaveData(virtualChester.getAdditionalSaveData());
      virtualChester.setAdditionalSaveData(null);
    }

    virtualChester.setIsSpawned(chesterEntity.getUUID());
    chesterEntity.tame(player);
    chesterEntity.moveTo(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0.0F, 0.0F);
    chesterEntity.setYawRotations(player.yRot - 180.0F);
    chesterEntity.setPrevYawRotations(chesterEntity.yRot);
    world.addFreshEntity(chesterEntity);
  }

  public void despawnChester(final ChesterEntity chester) {
    if (chester.level.isClientSide || chester.getOwnerUUID() == null) {
      return;
    }

    this.despawnChester((ServerWorld) chester.level, chester.getOwnerUUID());
  }

  public void despawnChester(final ServerWorld world, final UUID playerId) {
    final VirtualChester virtualChester = this.getVirtualChester(playerId);

    if (!virtualChester.isSpawned()) {
      return;
    }

    final ChesterEntity chesterEntity =
        (ChesterEntity) world.getEntity(virtualChester.getUniqueId());

    if (chesterEntity != null) {
      final CompoundNBT additionalSaveData = new CompoundNBT();
      chesterEntity.addAdditionalSaveData(additionalSaveData);
      virtualChester.setAdditionalSaveData(additionalSaveData);
      virtualChester.setIsDespawned();
      chesterEntity.remove();
      return;
    }

    final PlayerEntity player = world.getPlayerByUUID(playerId);
    final Position lastPos = virtualChester.getPosition();

    if (player != null) {
      if (lastPos == null) {
        player.sendMessage(
            new StringTextComponent(
                "The last know position of your Chester has not been saved. This could be a bug."),
            Util.NIL_UUID);
        return;
      }

      player.sendMessage(
          new StringTextComponent("You need to get closer to your Chester. Last know position:"),
          Util.NIL_UUID);
      player.sendMessage(new StringTextComponent(TextFormatting.GOLD + lastPos.toString()),
          Util.NIL_UUID);
    }
  }

  public void toggleChester(final PlayerEntity player, final BlockPos pos) {
    if (player.level.isClientSide) {
      return;
    }

    if (this.getVirtualChester(player.getUUID()).isSpawned()) {
      this.despawnChester((ServerWorld) player.level, player.getUUID());
    } else {
      this.spawnChester(player, pos);
    }
  }

  private void tickDeadTime() {
    final boolean wasResurrected = virtualChesterByPlayerId.values().stream().reduce(false,
        (wasResurrectedIn, virtualChester) -> {
          if (virtualChester.getDeadTime() > 0) {
            virtualChester.setDeadTime(virtualChester.getDeadTime() - TICK_DEAD_TIME_STEP);
            return wasResurrectedIn || virtualChester.getDeadTime() <= 0;
          }

          return wasResurrectedIn;
        }, Boolean::logicalOr);

    if (wasResurrected) {
      PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncVirtualChesterMessage());
    }
  }

  public void handleServerTick() {
    this.tickCount++;

    if (this.tickCount % TICK_DEAD_TIME_STEP == 0) {
      this.tickDeadTime();
    }
  }

  public void handleEyeBonePlacement(final PlayerEntity player, final BlockPos pos) {
    if (player.level.isClientSide) {
      return;
    }

    final VirtualChester virtualChester = this.getVirtualChester(player.getUUID());
    final ServerWorld world = (ServerWorld) player.level;

    if (!virtualChester.isSpawned()) {
      return;
    }

    final ChesterEntity chesterEntity =
        (ChesterEntity) world.getEntity(virtualChester.getUniqueId());

    if (chesterEntity == null) {
      return;
    }

    chesterEntity.setEyeBone(pos);

    player.sendMessage(
        new StringTextComponent(
            TextFormatting.GREEN + "A new Eye Bone position has been set for your Chester."),
        Util.NIL_UUID);
  }

  public void handleChesterDataSave(final ChesterEntity chester) {
    if (chester.getOwnerUUID() == null) {
      return;
    }

    this.getVirtualChester(chester.getOwnerUUID()).setPosition(chester);
  }

  public void handleChesterDeath(final ChesterEntity chester) {
    if (chester.getOwnerUUID() == null) {
      return;
    }

    final VirtualChester virtualChester = this.getVirtualChester(chester.getOwnerUUID());
    virtualChester.setIsDespawned();
    virtualChester.setDeadTime(chester.getDeathCooldown());

    PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncVirtualChesterMessage());
  }

  @Override
  public CompoundNBT save(final CompoundNBT compoundIn) {
    compoundIn.put(NbtKey.VIRTUAL_CHESTER_LIST, NbtUtil.serializeMap(this.virtualChesterByPlayerId,
        CompoundNBT::putUUID, (compound, key, value) -> compound.put(key, value.serializeNBT())));

    return compoundIn;
  }

  @Override
  public void load(final CompoundNBT compoundIn) {
    if (compoundIn.contains(NbtKey.VIRTUAL_CHESTER_LIST)) {
      this.virtualChesterByPlayerId.clear();
      this.virtualChesterByPlayerId.putAll(NbtUtil.deserializeMap(
          compoundIn.getList(NbtKey.VIRTUAL_CHESTER_LIST, Constants.NBT.TAG_COMPOUND),
          CompoundNBT::getUUID, (compound, key) -> new VirtualChester(compound.getCompound(key))));
    }
  }

  class NbtKey {
    public static final String VIRTUAL_CHESTER_LIST = "VirtualChesterList";
  }

  public class VirtualChester implements INBTSerializable<CompoundNBT> {
    private CompoundNBT additionalSaveData = null;
    private int deadTime = 0;
    private UUID uniqueId = null;
    private Position position = null;

    public VirtualChester() {}

    public VirtualChester(final CompoundNBT nbt) {
      this.deserializeNBT(nbt);
    }

    public boolean isDead() {
      return this.deadTime > 0;
    }

    public boolean isSpawned() {
      return this.uniqueId != null;
    }

    private void setIsSpawned(final UUID uuid) {
      this.setUniqueId(uuid);
    }

    private void setIsDespawned() {
      this.setUniqueId(null);
    }

    private CompoundNBT getAdditionalSaveData() {
      return this.additionalSaveData;
    }

    private void setAdditionalSaveData(final CompoundNBT compoundIn) {
      this.additionalSaveData = compoundIn;
      VirtualChesterSavedData.this.setDirty();
    }

    public int getDeadTime() {
      return this.deadTime;
    }

    private void setDeadTime(final int deadTime) {
      this.deadTime = deadTime;
      VirtualChesterSavedData.this.setDirty();
    }

    public UUID getUniqueId() {
      return this.uniqueId;
    }

    private void setUniqueId(final UUID uniqueId) {
      this.uniqueId = uniqueId;
      VirtualChesterSavedData.this.setDirty();
    }

    public Position getPosition() {
      return this.position;
    }

    private void setPosition(final ChesterEntity chester) {
      this.setPosition(chester.getX(), chester.getY(), chester.getZ(),
          chester.level.dimension().location().getPath());
    }

    private void setPosition(final double posX, final double posY, final double posZ,
        final String dim) {
      if (this.position == null) {
        this.position = new Position(posX, posY, posZ, dim);
      } else {
        this.position.setPosition(posX, posY, posZ, dim);
      }

      VirtualChesterSavedData.this.setDirty();
    }

    @Override
    public CompoundNBT serializeNBT() {
      final CompoundNBT compound = new CompoundNBT();

      if (this.additionalSaveData != null) {
        compound.put(NbtKey.ADDITIONAL_SAVE_DATA, this.additionalSaveData);
      }

      if (this.deadTime > 0) {
        compound.putInt(NbtKey.DEAD_TIME, this.deadTime);
      }

      if (this.uniqueId != null) {
        compound.putUUID(NbtKey.UNIQUE_ID, this.uniqueId);
      }

      if (this.position != null) {
        compound.put(NbtKey.POSITION, this.position.serializeNBT());
      }

      return compound;
    }

    @Override
    public void deserializeNBT(final CompoundNBT compoundIn) {
      if (compoundIn.contains(NbtKey.ADDITIONAL_SAVE_DATA)) {
        this.additionalSaveData = compoundIn.getCompound(NbtKey.ADDITIONAL_SAVE_DATA);
      }

      if (compoundIn.contains(NbtKey.DEAD_TIME)) {
        this.deadTime = compoundIn.getInt(NbtKey.DEAD_TIME);
      }

      if (compoundIn.hasUUID(NbtKey.UNIQUE_ID)) {
        this.uniqueId = compoundIn.getUUID(NbtKey.UNIQUE_ID);
      }

      if (compoundIn.contains(NbtKey.POSITION)) {
        this.position = new Position(compoundIn.getCompound(NbtKey.POSITION));
      }
    }

    class NbtKey {
      public static final String ADDITIONAL_SAVE_DATA = "AdditionalSaveData";
      public static final String DEAD_TIME = "DeadTime";
      public static final String UNIQUE_ID = "UniqueId";
      public static final String POSITION = "Position";
    }
  }
}
