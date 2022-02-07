package com.syllient.livingchest.saveddata;

import java.util.UUID;
import com.syllient.livingchest.util.Position;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public class WorldChester implements INBTSerializable<NBTTagCompound> {
  private int deadTime = 0;
  private UUID uniqueId = null;
  private Position position = null;
  private NBTTagCompound nbtData = null;

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

  public int getDeadTime() {
    return this.deadTime;
  }

  protected void setDeadTime(final int deadTime) {
    this.deadTime = deadTime;
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  protected void setUniqueId(final UUID uniqueId) {
    this.uniqueId = uniqueId;
  }

  public Position getPosition() {
    return this.position;
  }

  protected void setPosition(final double posX, final double posY, final double posZ,
      final int dim) {
    if (position == null) {
      this.position = new Position(posX, posY, posZ, dim);
    } else {
      this.position.setPosition(posX, posY, posZ, dim);
    }
  }

  public NBTTagCompound getNbtData() {
    return this.nbtData;
  }

  protected void setNbtData(final NBTTagCompound nbtData) {
    this.nbtData = nbtData;
  }

  @Override
  public NBTTagCompound serializeNBT() {
    final NBTTagCompound nbtCompound = new NBTTagCompound();

    if (this.deadTime > 0) {
      nbtCompound.setInteger(NbtKey.DEAD_TIME, this.deadTime);
    }

    if (this.uniqueId != null) {
      nbtCompound.setUniqueId(NbtKey.UNIQUE_ID, this.uniqueId);
    }

    if (this.position != null) {
      nbtCompound.setTag(NbtKey.POSITION, this.position.serializeNBT());
    }

    if (this.nbtData != null) {
      nbtCompound.setTag(NbtKey.NBT_DATA, this.nbtData);
    }

    return nbtCompound;
  }

  @Override
  public void deserializeNBT(final NBTTagCompound nbtCompoundIn) {
    if (nbtCompoundIn.hasKey(NbtKey.DEAD_TIME)) {
      this.deadTime = nbtCompoundIn.getInteger(NbtKey.DEAD_TIME);
    }

    if (nbtCompoundIn.hasUniqueId(NbtKey.UNIQUE_ID)) {
      this.uniqueId = nbtCompoundIn.getUniqueId(NbtKey.UNIQUE_ID);
    }

    if (nbtCompoundIn.hasKey(NbtKey.POSITION)) {
      this.position = new Position(nbtCompoundIn.getCompoundTag(NbtKey.POSITION));
    }

    if (nbtCompoundIn.hasKey(NbtKey.NBT_DATA)) {
      this.nbtData = nbtCompoundIn.getCompoundTag(NbtKey.NBT_DATA);
    }
  }

  class NbtKey {
    public static final String DEAD_TIME = "DeadTime";
    public static final String UNIQUE_ID = "UniqueId";
    public static final String POSITION = "Position";
    public static final String NBT_DATA = "NbtData";
  }
}
