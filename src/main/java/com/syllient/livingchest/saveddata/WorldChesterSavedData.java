package com.syllient.livingchest.saveddata;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.PacketHandler;
import com.syllient.livingchest.entity.ChesterEntity;
import com.syllient.livingchest.network.message.SyncWorldChesterSavedDataMessage;
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

public class WorldChesterSavedData extends WorldSavedData {
  private static final String DATA_NAME = LivingChest.MOD_ID + "_" + "chester";
  private static final int DEAD_TIME_DECREMENT_STEP = 5 * 20; // TODO: DECREMENT_STEP
  private final Map<UUID, WorldChester> worldChesterFromPlayerId = new HashMap<>();
  private int ticks = 0;

  public WorldChesterSavedData() {
    super(DATA_NAME);
  }

  public WorldChesterSavedData(final String name) {
    super(name);
  }

  public void onServerTick() {
    this.ticks++;

    if (this.ticks % DEAD_TIME_DECREMENT_STEP == 0) {
      this.onDecrementDeadTime();
    }
  }

  private void onDecrementDeadTime() {
    final boolean wasResurrected = this.worldChesterFromPlayerId.values().stream().reduce(false,
        (wasResurrectedIn, worldChester) -> {
          if (worldChester.getDeadTime() > 0) {
            worldChester.setDeadTime(worldChester.getDeadTime() - DEAD_TIME_DECREMENT_STEP);

            if (worldChester.getDeadTime() <= 0) {
              return true;
            }
          }

          return wasResurrectedIn;
        }, Boolean::logicalOr);

    if (wasResurrected) {
      this.onChesterResurrect();
    }
  }

  public void onChesterDie(final ChesterEntity chester) {
    if (chester.getOwnerId() == null) {
      return;
    }

    final WorldChester worldChester = this.getWorldChester(chester.getOwnerId());
    worldChester.setUniqueId(null);
    worldChester.setDeadTime(chester.getDeathCooldown());
    PacketHandler.INSTANCE.sendToAll(new SyncWorldChesterSavedDataMessage());
  }

  private void onChesterResurrect() {
    PacketHandler.INSTANCE.sendToAll(new SyncWorldChesterSavedDataMessage());
  }

  public void toggleChester(final EntityPlayer player, final World worldIn, final BlockPos pos) {
    if (worldIn.isRemote) {
      return;
    }

    if (this.getWorldChester(player.getUniqueID()).isSpawned()) {
      this.despawnChester(player, worldIn);
    } else {
      this.spawnChester(player, worldIn, pos);
    }
  }

  public void spawnChester(final EntityPlayer player, final World worldIn, final BlockPos pos) {
    if (worldIn.isRemote) {
      return;
    }

    final WorldChester worldChester = this.getWorldChester(player.getUniqueID());

    if (worldChester.isDead()) {
      final int minutes = (int) Math.ceil((float) worldChester.getDeadTime() / 20 / 60);

      player.sendMessage(new TextComponentString(
          new StringBuilder().append("Your Chester is dead. ").append(TextFormatting.RED)
              .append(minutes).append(" minute").append(minutes > 1 ? "s " : " ")
              .append(TextFormatting.WHITE).append("until you can summon him again.").toString()));

      return;
    }

    final ChesterEntity chesterEntity = new ChesterEntity(worldIn);
    chesterEntity.setTamedBy(player);
    chesterEntity.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0.0F,
        0.0F);
    worldChester.setUniqueId(chesterEntity.getUniqueID());

    if (worldChester.getInventory() != null) {
      chesterEntity.getInventory().deserializeNBT(worldChester.getInventory());
      worldChester.setInventory(null);
    }

    worldIn.spawnEntity(chesterEntity);
  }

  public void despawnChester(final EntityPlayer player, final World worldIn) {
    if (worldIn.isRemote) {
      return;
    }

    final WorldChester worldChester = this.getWorldChester(player.getUniqueID());
    final ChesterEntity chesterEntity =
        (ChesterEntity) WorldUtil.getEntityByUuid(worldIn, worldChester.getUniqueId());

    if (chesterEntity != null) {
      worldChester.setInventory(chesterEntity.getInventory().serializeNBT());
      worldChester.setUniqueId(null);
      chesterEntity.setDead();
    } else {
      player.sendMessage(
          new TextComponentString("Your Chester is out of range. Last know position:"));
      player.sendMessage(
          new TextComponentString(TextFormatting.GOLD + worldChester.getPosition().toString()));
    }
  }

  public WorldChester getWorldChester(final UUID playerId) {
    return this.worldChesterFromPlayerId.computeIfAbsent(playerId, (key) -> new WorldChester());
  }

  public static WorldChesterSavedData getInstance(final World world) {
    final MapStorage worldStorage = world.getMapStorage();

    return (WorldChesterSavedData) Optional
        .ofNullable(worldStorage.getOrLoadData(WorldChesterSavedData.class, DATA_NAME))
        .orElseGet(() -> {
          final WorldSavedData instance = new WorldChesterSavedData();
          worldStorage.setData(DATA_NAME, instance);
          return instance;
        });
  }

  @Override
  public NBTTagCompound writeToNBT(final NBTTagCompound nbtCompoundIn) {
    final NBTTagList nbtList = new NBTTagList();

    this.worldChesterFromPlayerId.forEach((playerId, worldChester) -> {
      final NBTTagCompound nbtCompound = new NBTTagCompound();
      nbtCompound.setUniqueId(NbtKey.PLAYER_ID, playerId);
      nbtCompound.setTag(NbtKey.WORLD_CHESTER, worldChester.serializeNBT());
      nbtList.appendTag(nbtCompound);
    });

    nbtCompoundIn.setTag(NbtKey.WORLD_CHESTER_NBT_LIST, nbtList);
    return nbtCompoundIn;
  }

  @Override
  public void readFromNBT(final NBTTagCompound nbtCompoundIn) {
    nbtCompoundIn.getTagList(NbtKey.WORLD_CHESTER_NBT_LIST, Constants.NBT.TAG_COMPOUND)
        .forEach((nbt) -> {
          final NBTTagCompound nbtCompound = (NBTTagCompound) nbt;

          this.worldChesterFromPlayerId.put(nbtCompound.getUniqueId(NbtKey.PLAYER_ID),
              new WorldChester(nbtCompound.getCompoundTag(NbtKey.WORLD_CHESTER)));
        });
  }

  class NbtKey {
    public static final String WORLD_CHESTER_NBT_LIST = "WorldChesterNbtList";
    public static final String PLAYER_ID = "PlayerId";
    public static final String WORLD_CHESTER = "WorldChester";
  }

  public class WorldChester implements INBTSerializable<NBTTagCompound> {
    private NBTTagCompound inventory = null;
    private int deadTime = 0;
    private UUID uniqueId = null;
    private Position position = null;

    public WorldChester() {}

    public WorldChester(final NBTTagCompound nbtCompoundIn) {
      this.deserializeNBT(nbtCompoundIn);
    }

    public boolean isSpawned() {
      return this.uniqueId != null;
    }

    public boolean isDead() {
      return this.deadTime > 0;
    }

    public NBTTagCompound getInventory() {
      return this.inventory;
    }

    private void setInventory(final NBTTagCompound inventory) {
      this.inventory = inventory;
      WorldChesterSavedData.this.markDirty();
    }

    public int getDeadTime() {
      return this.deadTime;
    }

    private void setDeadTime(final int deadTime) {
      this.deadTime = deadTime;
      WorldChesterSavedData.this.markDirty();
    }

    public UUID getUniqueId() {
      return this.uniqueId;
    }

    private void setUniqueId(final UUID uniqueId) {
      this.uniqueId = uniqueId;
      WorldChesterSavedData.this.markDirty();
    }

    public Position getPosition() {
      return this.position;
    }

    public void setPosition(final ChesterEntity chester) {
      this.setPosition(chester.posX, chester.posY, chester.posZ, chester.dimension);
    }

    private void setPosition(final double posX, final double posY, final double posZ,
        final int dim) {
      if (this.position == null) {
        this.position = new Position(posX, posY, posZ, dim);
      } else {
        this.position.setPosition(posX, posY, posZ, dim);
      }

      WorldChesterSavedData.this.markDirty();
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
