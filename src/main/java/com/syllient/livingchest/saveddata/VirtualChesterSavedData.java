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
import com.syllient.livingchest.util.WorldUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

public class VirtualChesterSavedData extends WorldSavedData {
  private static final String DATA_NAME = LivingChest.MOD_ID + "_" + "virtualchester";
  private static final int TICKS_DECREMENT_STEP = 5 * 20; // TODO: DECREMENT_STEP
  private final Map<UUID, VirtualChester> virtualChesterFromPlayerId = new HashMap<>();
  private int ticks = 0;

  public VirtualChesterSavedData() {
    super(DATA_NAME);
  }

  public VirtualChesterSavedData(final String name) {
    super(name);
  }

  public void onServerTick() {
    this.ticks++;

    if (this.ticks % TICKS_DECREMENT_STEP == 0) {
      this.reduceDeadTime();
    }
  }

  private void reduceDeadTime() {
    final boolean wasResurrected = this.virtualChesterFromPlayerId.values().stream().reduce(false,
        (wasResurrectedIn, virtualChester) -> {
          if (virtualChester.getDeadTime() > 0) {
            virtualChester.setDeadTime(virtualChester.getDeadTime() - TICKS_DECREMENT_STEP);

            if (virtualChester.getDeadTime() <= 0) {
              return true;
            }
          }

          return wasResurrectedIn;
        }, Boolean::logicalOr);

    if (wasResurrected) {
      this.onChesterResurrect();
    }
  }

  public void toggleChester(final EntityPlayer player, final World worldIn, final BlockPos pos) {
    if (this.getVirtualChester(player.getUniqueID()).isSpawned()) {
      this.despawnChester(player.getUniqueID(), worldIn);
    } else {
      this.spawnChester(player, worldIn, pos);
    }
  }

  public void spawnChester(final EntityPlayer player, final World worldIn, final BlockPos pos) {
    final VirtualChester virtualChester = this.getVirtualChester(player.getUniqueID());

    if (virtualChester.isDead()) {
      final int minutes = (int) Math.ceil((float) virtualChester.getDeadTime() / 20 / 60);

      player.sendMessage(new TextComponentString(new StringBuilder()
          .append("Ooh, looks like your Chester is dead. You still need to wait ")
          .append(TextFormatting.RED).append(minutes).append(" minute")
          .append(minutes > 1 ? "s " : " ").append(TextFormatting.WHITE)
          .append("before you can summon him again.").toString()));

      return;
    }

    final ChesterEntity chesterEntity = new ChesterEntity(worldIn);
    chesterEntity.setTamedBy(player);
    chesterEntity.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0.0F,
        0.0F);
    virtualChester.setIsSpawned(chesterEntity.getUniqueID());

    if (virtualChester.getInventory() != null) {
      chesterEntity.getInventory().deserializeNBT(virtualChester.getInventory());
      virtualChester.setInventory(null);
    }

    worldIn.spawnEntity(chesterEntity);
  }

  public void despawnChester(final UUID playerId, final World worldIn) {
    final VirtualChester virtualChester = this.getVirtualChester(playerId);
    final ChesterEntity chesterEntity =
        (ChesterEntity) WorldUtil.getEntityByUuid(worldIn, virtualChester.getUniqueId());

    if (chesterEntity != null) {
      virtualChester.setInventory(chesterEntity.getInventory().serializeNBT());
      virtualChester.setIsDespawned();
      chesterEntity.setDead();
      return;
    }

    final EntityPlayer player = worldIn.getPlayerEntityByUUID(playerId);

    if (player != null) {
      player.sendMessage(
          new TextComponentString("You need to get closer to your Chester. Last know position:"));
      player.sendMessage(
          new TextComponentString(TextFormatting.GOLD + virtualChester.getPosition().toString()));
    }
  }

  public void onPlaceEyeBone(final EntityPlayer player, final BlockPos pos) {
    final VirtualChester virtualChester = this.getVirtualChester(player.getUniqueID());

    if (!virtualChester.isSpawned()) {
      return;
    }

    final ChesterEntity chesterEntity =
        (ChesterEntity) WorldUtil.getEntityByUuid(player.world, virtualChester.getUniqueId());

    if (chesterEntity == null) {
      return;
    }

    chesterEntity.setEyeBone(pos);

    player.sendMessage(new TextComponentString(
        TextFormatting.GREEN + "A new Eye Bone position has been set for your Chester."));
  }

  private void onChesterResurrect() {
    PacketHandler.INSTANCE.sendToAll(new SyncVirtualChesterMessage());
  }

  public void onChesterDie(final ChesterEntity chester) {
    if (chester.getOwnerId() == null) {
      return;
    }

    final VirtualChester virtualChester = this.getVirtualChester(chester.getOwnerId());
    virtualChester.setIsDespawned();
    virtualChester.setDeadTime(chester.getDeathCooldown());

    PacketHandler.INSTANCE.sendToAll(new SyncVirtualChesterMessage());
  }

  public void onChesterSetDead(final ChesterEntity chester) {
    if (chester.getOwnerId() == null) {
      return;
    }

    this.getVirtualChester(chester.getOwnerId()).setIsDespawned();
  }

  public void onChesterWriteToNbt(final ChesterEntity chester) {
    if (chester.getOwnerId() == null) {
      return;
    }

    this.getVirtualChester(chester.getOwnerId()).setPosition(chester);
  }

  public VirtualChester getVirtualChester(final UUID playerId) {
    return this.virtualChesterFromPlayerId.computeIfAbsent(playerId, (key) -> new VirtualChester());
  }

  public static VirtualChesterSavedData getInstance(final World world) {
    final MapStorage worldStorage = world.getMapStorage();

    return (VirtualChesterSavedData) Optional
        .ofNullable(worldStorage.getOrLoadData(VirtualChesterSavedData.class, DATA_NAME))
        .orElseGet(() -> {
          final WorldSavedData instance = new VirtualChesterSavedData();
          worldStorage.setData(DATA_NAME, instance);
          return instance;
        });
  }

  @Override
  public NBTTagCompound writeToNBT(final NBTTagCompound nbtCompoundIn) {
    final NBTTagList nbtList = new NBTTagList();

    this.virtualChesterFromPlayerId.forEach((playerId, virtualChester) -> {
      final NBTTagCompound nbtCompound = new NBTTagCompound();
      nbtCompound.setUniqueId(NbtKey.PLAYER_ID, playerId);
      nbtCompound.setTag(NbtKey.VIRTUAL_CHESTER, virtualChester.serializeNBT());
      nbtList.appendTag(nbtCompound);
    });

    nbtCompoundIn.setTag(NbtKey.VIRTUAL_CHESTER_NBT_LIST, nbtList);
    return nbtCompoundIn;
  }

  @Override
  public void readFromNBT(final NBTTagCompound nbtCompoundIn) {
    nbtCompoundIn.getTagList(NbtKey.VIRTUAL_CHESTER_NBT_LIST, Constants.NBT.TAG_COMPOUND)
        .forEach((nbt) -> {
          final NBTTagCompound nbtCompound = (NBTTagCompound) nbt;

          this.virtualChesterFromPlayerId.put(nbtCompound.getUniqueId(NbtKey.PLAYER_ID),
              new VirtualChester(nbtCompound.getCompoundTag(NbtKey.VIRTUAL_CHESTER)));
        });
  }

  class NbtKey {
    public static final String VIRTUAL_CHESTER_NBT_LIST = "VirtualChesterNbtList";
    public static final String PLAYER_ID = "PlayerId";
    public static final String VIRTUAL_CHESTER = "VirtualChester";
  }

  public class VirtualChester implements INBTSerializable<NBTTagCompound> {
    private NBTTagCompound inventory = null;
    private int deadTime = 0;
    private UUID uniqueId = null;
    private Position position = null;

    public VirtualChester() {}

    public VirtualChester(final NBTTagCompound nbtCompoundIn) {
      this.deserializeNBT(nbtCompoundIn);
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
      this.setPosition(chester.posX, chester.posY, chester.posZ, chester.dimension);
    }

    private void setPosition(final double posX, final double posY, final double posZ,
        final int dim) {
      if (this.position == null) {
        this.position = new Position(posX, posY, posZ, dim);
      } else {
        this.position.setPosition(posX, posY, posZ, dim);
      }

      VirtualChesterSavedData.this.markDirty();
    }

    @Override
    public NBTTagCompound serializeNBT() {
      final NBTTagCompound nbtCompound = new NBTTagCompound();

      if (this.inventory != null) {
        nbtCompound.setTag(NbtKey.INVENTORY, this.inventory);
      }

      if (this.deadTime > 0) {
        nbtCompound.setInteger(NbtKey.DEAD_TIME, this.deadTime);
      }

      if (this.uniqueId != null) {
        nbtCompound.setUniqueId(NbtKey.UNIQUE_ID, this.uniqueId);
      }

      if (this.position != null) {
        nbtCompound.setTag(NbtKey.POSITION, this.position.serializeNBT());
      }

      return nbtCompound;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound nbtCompoundIn) {
      if (nbtCompoundIn.hasKey(NbtKey.INVENTORY)) {
        this.inventory = nbtCompoundIn.getCompoundTag(NbtKey.INVENTORY);
      }

      if (nbtCompoundIn.hasKey(NbtKey.DEAD_TIME)) {
        this.deadTime = nbtCompoundIn.getInteger(NbtKey.DEAD_TIME);
      }

      if (nbtCompoundIn.hasUniqueId(NbtKey.UNIQUE_ID)) {
        this.uniqueId = nbtCompoundIn.getUniqueId(NbtKey.UNIQUE_ID);
      }

      if (nbtCompoundIn.hasKey(NbtKey.POSITION)) {
        this.position = new Position(nbtCompoundIn.getCompoundTag(NbtKey.POSITION));
      }
    }

    class NbtKey {
      public static final String INVENTORY = "Inventory";
      public static final String DEAD_TIME = "DeadTime";
      public static final String UNIQUE_ID = "UniqueId";
      public static final String POSITION = "Position";
    }
  }
}
