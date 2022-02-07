package com.syllient.livingchest.saveddata;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.PacketHandler;
import com.syllient.livingchest.entity.ChesterEntity;
import com.syllient.livingchest.network.message.SyncWorldChesterSavedDataMessage;
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
            this.markDirty();

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
    this.markDirty();
    PacketHandler.INSTANCE.sendToAll(new SyncWorldChesterSavedDataMessage());
  }

  private void onChesterResurrect() {
    PacketHandler.INSTANCE.sendToAll(new SyncWorldChesterSavedDataMessage());
  }

  public void saveChesterPosition(final ChesterEntity chester) {
    if (chester.getOwnerId() != null) {
      this.getWorldChester(chester.getOwnerId()).setPosition(chester.posX, chester.posY,
          chester.posZ, chester.dimension);
      this.markDirty();
    }
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

    if (worldChester.getNbtData() != null) {
      chesterEntity.readFromWorldChesterNbt(worldChester.getNbtData());
    }

    worldChester.setUniqueId(chesterEntity.getUniqueID());
    this.markDirty();
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
      worldChester.setNbtData(chesterEntity.writeToWorldChesterNbt(new NBTTagCompound()));
      worldChester.setUniqueId(null);
      chesterEntity.setDead();
      this.markDirty();
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
}
