package com.syllient.livingchest.saveddata;

import java.util.HashMap;
import java.util.UUID;
import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.entity.ChesterEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

// [['uuidPlayer', { x: 30, y: 30, z: 30, dim: 1 }], ['uuidPlayer', { x: 30, y: 30, z: 30, dim: 1
// }]]

// [['uuidPlayer', 1000]]

public class ChesterSavedData extends WorldSavedData {
  private static final String DATA_NAME = LivingChest.MOD_ID + "_" + "chester";
  private final HashMap<UUID, Integer> ticksUntilChesterLive = new HashMap<>();

  public ChesterSavedData() {
    super(DATA_NAME);
  }

  public ChesterSavedData(final String name) {
    super(name);
  }

  // public void onTick() {
  // // fazer isso só a cada 20 ticks? ou algo assim

  // if (this.ticksUntilChesterLive.size() > 0) {
  // // MutableInt
  // // this.ticksUntilChesterLive.values().stream().forEach...
  // this.markDirty();
  // }
  // }

  public boolean isChesterDead(final EntityPlayer player) {
    return this.ticksUntilChesterLive.get(player.getUniqueID()) != null;
  }

  public void onChesterDie(final ChesterEntity chester) {
    if (chester.getOwnerId() != null) {
      this.ticksUntilChesterLive.put(chester.getOwnerId(), chester.getDeathCooldown());
      this.markDirty();
    }
  }

  @Override
  public NBTTagCompound writeToNBT(final NBTTagCompound compound) {
    return compound;
  }

  @Override
  public void readFromNBT(final NBTTagCompound nbt) {}

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
