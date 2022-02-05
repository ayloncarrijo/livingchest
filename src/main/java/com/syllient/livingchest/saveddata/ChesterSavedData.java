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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class ChesterSavedData extends WorldSavedData {
  private static final String DATA_NAME = LivingChest.MOD_ID + "_" + "chester";
  private static final int DEATH_DECREASE_STEP = 100;
  private int ticks = 0;
  // private final Map<UUID, Position> positions = new HashMap<>();
  private Map<UUID, MutableInt> deathTimes = new HashMap<>();

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

      this.markDirty();
    }
  }

  private void decreaseDeathTimes() {
    this.deathTimes = this.deathTimes.entrySet().stream().filter((entry) -> {
      entry.getValue().decrement(DEATH_DECREASE_STEP);
      return entry.getValue().getInt() > 0;
    }).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }

  public boolean isChesterDead(final EntityPlayer player) {
    return this.isChesterDead(player.getUniqueID());
  }

  public boolean isChesterDead(final UUID uuid) {
    return this.deathTimes.containsKey(uuid);
  }

  public void onChesterDie(final ChesterEntity chester) {
    if (chester.getOwnerId() != null) {
      this.deathTimes.put(chester.getOwnerId(), new MutableInt(chester.getDeathCooldown()));
      this.markDirty();
      PacketHandler.INSTANCE.sendToAll(new SyncChesterSavedDataMessage());
    }
  }

  public void onChesterLive() {
    PacketHandler.INSTANCE.sendToAll(new SyncChesterSavedDataMessage());
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
