package com.syllient.livingchest.saveddata;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.PacketHandler;
import com.syllient.livingchest.entity.ChesterEntity;
import com.syllient.livingchest.network.message.SyncChesterSavedDataMessage;
import com.syllient.livingchest.util.MutableInt;
import com.syllient.livingchest.util.Position;
import com.syllient.livingchest.util.WorldUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class ChesterSavedData extends WorldSavedData {
  private static final String DATA_NAME = LivingChest.MOD_ID + "_" + "chester";
  private static final int DEATH_DECREASE_STEP = 100; // TODO: DEATH_DECREASE_STEP
  private final Map<UUID, UUID> spawnedChesters = new HashMap<>();
  private final Map<UUID, Position> positions = new HashMap<>();
  private final Map<UUID, NBTTagCompound> nbts = new HashMap<>();
  private Map<UUID, MutableInt> deathTimes = new HashMap<>();
  private int ticks = 0;

  public ChesterSavedData() {
    super(DATA_NAME);
  }

  public ChesterSavedData(final String name) {
    super(name);
  }

  public void onServerTick() {
    this.ticks++;

    if (this.ticks % DEATH_DECREASE_STEP == 0 && this.deathTimes.size() > 0) {
      final int previousSize = this.deathTimes.size();
      this.decreaseDeathTimes();
      final int currentSize = this.deathTimes.size();

      if (previousSize != currentSize) {
        this.onChesterLive();
      }
    }
  }

  private void decreaseDeathTimes() {
    this.deathTimes = this.deathTimes.entrySet().stream().filter((entry) -> {
      entry.getValue().decrement(DEATH_DECREASE_STEP);
      return entry.getValue().getInt() > 0;
    }).collect(Collectors.toMap(Entry::getKey, Entry::getValue));

    this.markDirty();
  }

  public int getDeathTimeLeft(final EntityPlayer player) {
    return this.getDeathTimeLeft(player.getUniqueID());
  }

  public int getDeathTimeLeft(final UUID uuid) {
    return deathTimes.containsKey(uuid) ? deathTimes.get(uuid).getInt() : 0;
  }

  public void saveChesterPosition(final ChesterEntity chester) {
    if (chester.getOwnerId() != null) {
      this.positions.put(chester.getOwnerId(),
          new Position(chester.posX, chester.posY, chester.posZ, chester.dimension));
      this.markDirty();
    }
  }

  public boolean isChesterDead(final EntityPlayer player) {
    return this.isChesterDead(player.getUniqueID());
  }

  public boolean isChesterDead(final UUID uuid) {
    return this.deathTimes.containsKey(uuid);
  }

  public void onChesterDie(final ChesterEntity chester) {
    if (chester.getOwnerId() != null) {
      this.spawnedChesters.remove(chester.getOwnerId());
      this.deathTimes.put(chester.getOwnerId(), new MutableInt(chester.getDeathCooldown()));
      this.markDirty();
      PacketHandler.INSTANCE.sendToAll(new SyncChesterSavedDataMessage());
    }
  }

  public void onChesterLive() {
    PacketHandler.INSTANCE.sendToAll(new SyncChesterSavedDataMessage());
  }

  public void toggleChester(final EntityPlayer player, final World worldIn, final BlockPos pos) {
    if (worldIn.isRemote) {
      return;
    }

    if (spawnedChesters.containsKey(player.getUniqueID())) {
      this.despawnChester(player, worldIn);
    } else {
      this.spawnChester(player, worldIn, pos);
    }
  }

  public void spawnChester(final EntityPlayer player, final World worldIn, final BlockPos pos) {
    if (this.isChesterDead(player)) {
      final int minutes = (int) Math.ceil((float) this.getDeathTimeLeft(player) / 20 / 60);

      player.sendMessage(new TextComponentString(
          new StringBuilder().append("Your Chester is dead. ").append(TextFormatting.RED)
              .append(minutes).append(" minute").append(minutes > 1 ? "s " : " ")
              .append(TextFormatting.WHITE).append("until you can summon him again.").toString()));

      return;
    }

    final ChesterEntity chester = new ChesterEntity(worldIn);
    chester.setTamedBy(player);
    chester.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0.0F, 0.0F);

    if (this.nbts.containsKey(player.getUniqueID())) {
      chester.readFromNbtWhenSpawn(this.nbts.get(player.getUniqueID()));
      this.nbts.remove(player.getUniqueID());
    }

    worldIn.spawnEntity(chester);
    this.spawnedChesters.put(player.getUniqueID(), chester.getUniqueID());
    this.markDirty();
  }

  public void despawnChester(final EntityPlayer player, final World worldIn) {
    final ChesterEntity chester = (ChesterEntity) WorldUtil.getEntityByUuid(worldIn,
        this.spawnedChesters.get(player.getUniqueID()));

    if (chester != null) {
      this.nbts.put(player.getUniqueID(), chester.writeToNbtToDespawn(new NBTTagCompound()));
      this.spawnedChesters.remove(player.getUniqueID());
      this.markDirty();
      chester.setDead();
    } else {
      player.sendMessage(
          new TextComponentString("Your Chester is out of range. Last know position:"));
      player.sendMessage(new TextComponentString(
          TextFormatting.GOLD + this.positions.get(player.getUniqueID()).toString()));
    }
  }

  @Override
  public NBTTagCompound writeToNBT(final NBTTagCompound compound) {
    this.writeDeathTimes(compound);
    return compound;
  }

  @Override
  public void readFromNBT(final NBTTagCompound compound) {
    this.readDeathTimes(compound);
  }

  public NBTTagCompound getUpdateTag() {
    final NBTTagCompound compound = new NBTTagCompound();

    this.writeDeathTimes(compound);
    return compound;
  }

  public void handleUpdateTag(final NBTTagCompound compound) {
    this.readDeathTimes(compound);
  }

  private NBTTagCompound writeDeathTimes(final NBTTagCompound compoundIn) {
    if (this.deathTimes == null) {
      return compoundIn;
    }

    final NBTTagCompound compound = new NBTTagCompound();

    this.deathTimes.entrySet().stream().forEach(
        (entry) -> compound.setInteger(entry.getKey().toString(), entry.getValue().getInt()));

    compoundIn.setTag("DeathTimes", compound);
    return compoundIn;
  }

  private void readDeathTimes(final NBTTagCompound compoundIn) {
    if (!compoundIn.hasKey("DeathTimes")) {
      return;
    }

    final NBTTagCompound compound = compoundIn.getCompoundTag("DeathTimes");

    this.deathTimes = compound.getKeySet().stream().collect(
        Collectors.toMap(UUID::fromString, (key) -> new MutableInt(compound.getInteger(key))));
  }

  public static ChesterSavedData get(final World world) {
    final MapStorage worldStorage = world.getMapStorage();
    ChesterSavedData instance =
        (ChesterSavedData) worldStorage.getOrLoadData(ChesterSavedData.class, DATA_NAME);

    if (instance == null) {
      instance = new ChesterSavedData();
      worldStorage.setData(DATA_NAME, instance);
    }

    return instance;
  }
}
