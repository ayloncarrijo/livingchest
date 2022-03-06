package com.syllient.livingchest.saveddata;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.entity.ChesterEntity;
import com.syllient.livingchest.eventhandler.registry.EntityRegistry;
import com.syllient.livingchest.util.Position;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

public class VirtualChesterSavedData extends WorldSavedData {
  private static final String ID = LivingChest.MOD_ID + "_" + "virtualchester";
  private static final VirtualChesterSavedData INSTANCE = new VirtualChesterSavedData();
  private static final int TICKS_DECREASE_DEAD_TIME_STEP = 600;

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

    return ((ServerWorld) world).getChunkSource().getDataStorage()
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

  public void toggleChester(final PlayerEntity player, final BlockPos pos) {
    if (player.level.isClientSide) {
      return;
    }

    final ServerWorld world = (ServerWorld) player.level;

    if (this.getVirtualChester(player.getUUID()).isSpawned()) {
      this.despawnChester(world, player.getUUID());
    } else {
      this.spawnChester(player, pos);
    }
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
    chesterEntity.tame(player);
    chesterEntity.moveTo(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0.0F, 0.0F);
    chesterEntity.setYawRotations(player.yRot - 180.0F);
    chesterEntity.setPrevYawRotations(chesterEntity.yRot);

    if (virtualChester.getInventory() != null) {
      chesterEntity.getInventory().deserializeNBT(virtualChester.getInventory());
      virtualChester.setInventory(null);
    }

    if (virtualChester.getHealth() > 0.0F) {
      chesterEntity.setHealth(virtualChester.getHealth());
      virtualChester.setHealth(0.0F);
    }

    virtualChester.setIsSpawned(chesterEntity.getUUID());
    world.addFreshEntity(chesterEntity);
  }

  public void despawnChester(final ServerWorld world, final UUID playerId) {
    final VirtualChester virtualChester = this.getVirtualChester(playerId);

    if (!virtualChester.isSpawned()) {
      return;
    }

    final ChesterEntity chesterEntity =
        (ChesterEntity) world.getEntity(virtualChester.getUniqueId());

    if (chesterEntity != null) {
      virtualChester.setInventory(chesterEntity.getInventory().serializeNBT());
      virtualChester.setHealth(chesterEntity.getHealth());
      virtualChester.setIsDespawned();
      chesterEntity.remove();
      return;
    }

    final PlayerEntity player = world.getPlayerByUUID(playerId);

    if (player != null) {
      player.sendMessage(
          new StringTextComponent("You need to get closer to your Chester. Last know position:"),
          Util.NIL_UUID);
      player.sendMessage(
          new StringTextComponent(TextFormatting.GOLD + virtualChester.getPosition().toString()),
          Util.NIL_UUID);
    }

  }

  public void handleServerTick() {
    this.tickCount++;

    if (this.tickCount % TICKS_DECREASE_DEAD_TIME_STEP == 0) {
      this.decreaseDeadTime();
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

  public void handleChesterSave(final ChesterEntity chester) {
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

    // PacketHandler.INSTANCE.sendToAll(new SyncVirtualChesterMessage());
  }

  public void handleChesterRemoval(final ChesterEntity chester) {
    if (chester.getOwnerUUID() == null) {
      return;
    }

    this.getVirtualChester(chester.getOwnerUUID()).setIsDespawned();
  }

  private void decreaseDeadTime() {
    final boolean wasResurrected = this.virtualChesterByPlayerId.values().stream().reduce(false,
        (wasResurrectedIn, virtualChester) -> {
          if (virtualChester.getDeadTime() > 0) {
            virtualChester
                .setDeadTime(virtualChester.getDeadTime() - TICKS_DECREASE_DEAD_TIME_STEP);

            if (virtualChester.getDeadTime() <= 0) {
              return true;
            }
          }

          return wasResurrectedIn;
        }, Boolean::logicalOr);

    if (wasResurrected) {
      // PacketHandler.INSTANCE.sendToAll(new SyncVirtualChesterMessage());
    }
  }

  @Override
  public CompoundNBT save(final CompoundNBT compoundIn) {
    final ListNBT list = new ListNBT();

    this.virtualChesterByPlayerId.forEach((playerId, virtualChester) -> {
      final CompoundNBT compound = new CompoundNBT();
      compound.putUUID(NbtKey.PLAYER_ID, playerId);
      compound.put(NbtKey.VIRTUAL_CHESTER, virtualChester.serializeNBT());
      list.add(compound);
    });

    compoundIn.put(NbtKey.VIRTUAL_CHESTER_TAG_LIST, list);
    return compoundIn;
  }

  @Override
  public void load(final CompoundNBT compoundIn) {
    compoundIn.getList(NbtKey.VIRTUAL_CHESTER_TAG_LIST, Constants.NBT.TAG_COMPOUND)
        .forEach((nbt) -> {
          final CompoundNBT compound = (CompoundNBT) nbt;
          this.virtualChesterByPlayerId.put(compound.getUUID(NbtKey.PLAYER_ID),
              new VirtualChester(compound.getCompound(NbtKey.VIRTUAL_CHESTER)));
        });
  }

  class NbtKey {
    public static final String VIRTUAL_CHESTER_TAG_LIST = "VirtualChesterTagList";
    public static final String PLAYER_ID = "PlayerId";
    public static final String VIRTUAL_CHESTER = "VirtualChester";
  }

  public class VirtualChester implements INBTSerializable<CompoundNBT> {
    private CompoundNBT inventory = null;
    private int deadTime = 0;
    private float health = 0.0F;
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

    public CompoundNBT getInventory() {
      return this.inventory;
    }

    private void setInventory(final CompoundNBT inventory) {
      this.inventory = inventory;
      VirtualChesterSavedData.this.setDirty();
    }

    public int getDeadTime() {
      return this.deadTime;
    }

    private void setDeadTime(final int deadTime) {
      this.deadTime = deadTime;
      VirtualChesterSavedData.this.setDirty();
    }

    public float getHealth() {
      return this.health;
    }

    private void setHealth(final float health) {
      this.health = health;
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
          chester.level.dimension().getRegistryName().getPath());
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

      if (this.inventory != null) {
        compound.put(NbtKey.INVENTORY, this.inventory);
      }

      if (this.deadTime > 0) {
        compound.putInt(NbtKey.DEAD_TIME, this.deadTime);
      }

      if (this.health > 0.0F) {
        compound.putFloat(NbtKey.HEALTH, this.health);
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
      if (compoundIn.contains(NbtKey.INVENTORY)) {
        this.inventory = compoundIn.getCompound(NbtKey.INVENTORY);
      }

      if (compoundIn.contains(NbtKey.DEAD_TIME)) {
        this.deadTime = compoundIn.getInt(NbtKey.DEAD_TIME);
      }

      if (compoundIn.contains(NbtKey.HEALTH)) {
        this.health = compoundIn.getFloat(NbtKey.HEALTH);
      }

      if (compoundIn.hasUUID(NbtKey.UNIQUE_ID)) {
        this.uniqueId = compoundIn.getUUID(NbtKey.UNIQUE_ID);
      }

      if (compoundIn.contains(NbtKey.POSITION)) {
        this.position = new Position(compoundIn.getCompound(NbtKey.POSITION));
      }
    }

    class NbtKey {
      public static final String INVENTORY = "Inventory";
      public static final String DEAD_TIME = "DeadTime";
      public static final String HEALTH = "Health";
      public static final String UNIQUE_ID = "UniqueId";
      public static final String POSITION = "Position";
    }
  }
}
