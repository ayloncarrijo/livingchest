package com.syllient.livingchest.saveddata;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.PacketHandler;
import com.syllient.livingchest.entity.ChesterEntity;
import com.syllient.livingchest.network.message.SyncVirtualChesterMessage;
import com.syllient.livingchest.util.Position;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
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
    if (world.isRemote) {
      throw new IllegalArgumentException("The world must be an instance of ServerWorld.");
    }

    final MapStorage worldStorage = world.getMapStorage();

    return (VirtualChesterSavedData) Optional
        .ofNullable(worldStorage.getOrLoadData(VirtualChesterSavedData.class, ID)).orElseGet(() -> {
          final WorldSavedData instance = new VirtualChesterSavedData();
          worldStorage.setData(ID, instance);
          return instance;
        });
  }

  public static VirtualChesterSavedData getClientInstance(final World world) {
    if (!world.isRemote) {
      throw new IllegalArgumentException("The world must be an instance of ClientWorld.");
    }

    return INSTANCE;
  }

  public VirtualChester getVirtualChester(final UUID playerId) {
    return this.virtualChesterByPlayerId.computeIfAbsent(playerId, (key) -> new VirtualChester());
  }

  public void toggleChester(final EntityPlayer player, final BlockPos pos) {
    if (player.world.isRemote) {
      return;
    }

    final WorldServer world = (WorldServer) player.world;

    if (this.getVirtualChester(player.getUniqueID()).isSpawned()) {
      this.despawnChester(world, player.getUniqueID());
    } else {
      this.spawnChester(player, pos);
    }
  }

  public void spawnChester(final EntityPlayer player, final BlockPos pos) {
    if (player.world.isRemote) {
      return;
    }

    final VirtualChester virtualChester = this.getVirtualChester(player.getUniqueID());
    final WorldServer world = (WorldServer) player.world;

    if (virtualChester.isSpawned()) {
      return;
    }

    if (virtualChester.isDead()) {
      final int minutes = (int) Math.ceil((float) virtualChester.getDeadTime() / 20 / 60);

      player.sendMessage(new TextComponentString(new StringBuilder()
          .append("Ooh, looks like your Chester is dead. You still need to wait ")
          .append(TextFormatting.RED).append(minutes).append(" minute")
          .append(minutes > 1 ? "s " : " ").append(TextFormatting.WHITE)
          .append("before you can summon him again.").toString()));

      return;
    }

    if (!world.isAirBlock(pos.up())) {
      return;
    }

    final ChesterEntity chesterEntity = new ChesterEntity(world);
    chesterEntity.setTamedBy(player);
    chesterEntity.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0.0F,
        0.0F);
    chesterEntity.setYawRotations(player.rotationYaw - 180.0F);
    chesterEntity.setPrevYawRotations(chesterEntity.rotationYaw);

    if (virtualChester.getInventory() != null) {
      chesterEntity.getInventory().deserializeNBT(virtualChester.getInventory());
      virtualChester.setInventory(null);
    }

    if (virtualChester.getHealth() > 0.0F) {
      chesterEntity.setHealth(virtualChester.getHealth());
      virtualChester.setHealth(0.0F);
    }

    virtualChester.setIsSpawned(chesterEntity.getUniqueID());
    world.spawnEntity(chesterEntity);
  }

  public void despawnChester(final WorldServer world, final UUID playerId) {
    final VirtualChester virtualChester = this.getVirtualChester(playerId);

    if (!virtualChester.isSpawned()) {
      return;
    }

    final ChesterEntity chesterEntity =
        (ChesterEntity) world.getEntityFromUuid(virtualChester.getUniqueId());

    if (chesterEntity != null) {
      virtualChester.setInventory(chesterEntity.getInventory().serializeNBT());
      virtualChester.setHealth(chesterEntity.getHealth());
      virtualChester.setIsDespawned();
      chesterEntity.setDead();
      return;
    }

    final EntityPlayer player = world.getPlayerEntityByUUID(playerId);

    if (player != null) {
      player.sendMessage(
          new TextComponentString("You need to get closer to your Chester. Last know position:"));
      player.sendMessage(
          new TextComponentString(TextFormatting.GOLD + virtualChester.getPosition().toString()));
    }
  }

  public void handleServerTick() {
    this.tickCount++;

    if (this.tickCount % TICKS_DECREASE_DEAD_TIME_STEP == 0) {
      this.decreaseDeadTime();
    }
  }

  public void handleEyeBonePlacement(final EntityPlayer player, final BlockPos pos) {
    if (player.world.isRemote) {
      return;
    }

    final VirtualChester virtualChester = this.getVirtualChester(player.getUniqueID());
    final WorldServer world = (WorldServer) player.world;

    if (!virtualChester.isSpawned()) {
      return;
    }

    final ChesterEntity chesterEntity =
        (ChesterEntity) world.getEntityFromUuid(virtualChester.getUniqueId());

    if (chesterEntity == null) {
      return;
    }

    chesterEntity.setEyeBone(pos);

    player.sendMessage(new TextComponentString(
        TextFormatting.GREEN + "A new Eye Bone position has been set for your Chester."));
  }

  public void handleChesterSave(final ChesterEntity chester) {
    if (chester.getOwnerId() == null) {
      return;
    }

    this.getVirtualChester(chester.getOwnerId()).setPosition(chester);
  }

  public void handleChesterDeath(final ChesterEntity chester) {
    if (chester.getOwnerId() == null) {
      return;
    }

    final VirtualChester virtualChester = this.getVirtualChester(chester.getOwnerId());
    virtualChester.setIsDespawned();
    virtualChester.setDeadTime(chester.getDeathCooldown());

    PacketHandler.INSTANCE.sendToAll(new SyncVirtualChesterMessage());
  }

  public void handleChesterRemoval(final ChesterEntity chester) {
    if (chester.getOwnerId() == null) {
      return;
    }

    this.getVirtualChester(chester.getOwnerId()).setIsDespawned();
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
      PacketHandler.INSTANCE.sendToAll(new SyncVirtualChesterMessage());
    }
  }

  @Override
  public NBTTagCompound writeToNBT(final NBTTagCompound compoundIn) {
    final NBTTagList list = new NBTTagList();

    this.virtualChesterByPlayerId.forEach((playerId, virtualChester) -> {
      final NBTTagCompound compound = new NBTTagCompound();
      compound.setUniqueId(NbtKey.PLAYER_ID, playerId);
      compound.setTag(NbtKey.VIRTUAL_CHESTER, virtualChester.serializeNBT());
      list.appendTag(compound);
    });

    compoundIn.setTag(NbtKey.VIRTUAL_CHESTER_TAG_LIST, list);
    return compoundIn;
  }

  @Override
  public void readFromNBT(final NBTTagCompound compoundIn) {
    compoundIn.getTagList(NbtKey.VIRTUAL_CHESTER_TAG_LIST, Constants.NBT.TAG_COMPOUND)
        .forEach((nbt) -> {
          final NBTTagCompound compound = (NBTTagCompound) nbt;
          this.virtualChesterByPlayerId.put(compound.getUniqueId(NbtKey.PLAYER_ID),
              new VirtualChester(compound.getCompoundTag(NbtKey.VIRTUAL_CHESTER)));
        });
  }

  class NbtKey {
    public static final String VIRTUAL_CHESTER_TAG_LIST = "VirtualChesterTagList";
    public static final String PLAYER_ID = "PlayerId";
    public static final String VIRTUAL_CHESTER = "VirtualChester";
  }

  public class VirtualChester implements INBTSerializable<NBTTagCompound> {
    private NBTTagCompound inventory = null;
    private int deadTime = 0;
    private float health = 0.0F;
    private UUID uniqueId = null;
    private Position position = null;

    public VirtualChester() {}

    public VirtualChester(final NBTTagCompound tagCompoundIn) {
      this.deserializeNBT(tagCompoundIn);
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

    public NBTTagCompound getInventory() {
      return this.inventory;
    }

    private void setInventory(final NBTTagCompound inventory) {
      this.inventory = inventory;
      VirtualChesterSavedData.this.markDirty();
    }

    public int getDeadTime() {
      return this.deadTime;
    }

    private void setDeadTime(final int deadTime) {
      this.deadTime = deadTime;
      VirtualChesterSavedData.this.markDirty();
    }

    public float getHealth() {
      return this.health;
    }

    private void setHealth(final float health) {
      this.health = health;
      VirtualChesterSavedData.this.markDirty();
    }

    public UUID getUniqueId() {
      return this.uniqueId;
    }

    private void setUniqueId(final UUID uniqueId) {
      this.uniqueId = uniqueId;
      VirtualChesterSavedData.this.markDirty();
    }

    public Position getPosition() {
      return this.position;
    }

    private void setPosition(final ChesterEntity chester) {
      this.setPosition(chester.posX, chester.posY, chester.posZ,
          DimensionManager.getProviderType(chester.dimension).getName());
    }

    private void setPosition(final double posX, final double posY, final double posZ,
        final String dim) {
      if (this.position == null) {
        this.position = new Position(posX, posY, posZ, dim);
      } else {
        this.position.setPosition(posX, posY, posZ, dim);
      }

      VirtualChesterSavedData.this.markDirty();
    }

    @Override
    public NBTTagCompound serializeNBT() {
      final NBTTagCompound compound = new NBTTagCompound();

      if (this.inventory != null) {
        compound.setTag(NbtKey.INVENTORY, this.inventory);
      }

      if (this.deadTime > 0) {
        compound.setInteger(NbtKey.DEAD_TIME, this.deadTime);
      }

      if (this.health > 0.0F) {
        compound.setFloat(NbtKey.HEALTH, this.health);
      }

      if (this.uniqueId != null) {
        compound.setUniqueId(NbtKey.UNIQUE_ID, this.uniqueId);
      }

      if (this.position != null) {
        compound.setTag(NbtKey.POSITION, this.position.serializeNBT());
      }

      return compound;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound compoundIn) {
      if (compoundIn.hasKey(NbtKey.INVENTORY)) {
        this.inventory = compoundIn.getCompoundTag(NbtKey.INVENTORY);
      }

      if (compoundIn.hasKey(NbtKey.DEAD_TIME)) {
        this.deadTime = compoundIn.getInteger(NbtKey.DEAD_TIME);
      }

      if (compoundIn.hasKey(NbtKey.HEALTH)) {
        this.health = compoundIn.getFloat(NbtKey.HEALTH);
      }

      if (compoundIn.hasUniqueId(NbtKey.UNIQUE_ID)) {
        this.uniqueId = compoundIn.getUniqueId(NbtKey.UNIQUE_ID);
      }

      if (compoundIn.hasKey(NbtKey.POSITION)) {
        this.position = new Position(compoundIn.getCompoundTag(NbtKey.POSITION));
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
