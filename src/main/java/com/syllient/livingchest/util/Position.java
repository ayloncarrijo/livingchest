package com.syllient.livingchest.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.INBTSerializable;

public class Position implements INBTSerializable<NBTTagCompound> {
  private double posX;
  private double posY;
  private double posZ;
  private int dim;

  public Position(final double posX, final double posY, final double posZ, final int dim) {
    this.setPosition(posX, posY, posZ, dim);
  }

  public Position(final NBTTagCompound nbtCompound) {
    this.deserializeNBT(nbtCompound);
  }

  public double getPosX() {
    return this.posX;
  }

  public double getPosY() {
    return this.posY;
  }

  public double getPosZ() {
    return this.posZ;
  }

  public int getDim() {
    return this.dim;
  }

  public void setPosition(final double posX, final double posY, final double posZ, final int dim) {
    this.posX = posX;
    this.posY = posY;
    this.posZ = posZ;
    this.dim = dim;
  }

  @Override
  public NBTTagCompound serializeNBT() {
    final NBTTagCompound nbt = new NBTTagCompound();

    nbt.setDouble(NbtKey.POS_X, this.posX);
    nbt.setDouble(NbtKey.POS_Y, this.posY);
    nbt.setDouble(NbtKey.POS_Z, this.posZ);
    nbt.setInteger(NbtKey.DIM, this.dim);

    return nbt;
  }

  @Override
  public void deserializeNBT(final NBTTagCompound nbt) {
    this.posX = nbt.getDouble(NbtKey.POS_X);
    this.posY = nbt.getDouble(NbtKey.POS_Y);
    this.posZ = nbt.getDouble(NbtKey.POS_Z);
    this.dim = nbt.getInteger(NbtKey.DIM);
  }

  @Override
  public String toString() {
    return new StringBuilder().append("X: ").append((int) this.posX).append(" / ").append("Y: ")
        .append((int) this.posY).append(" / ").append("Z: ").append((int) this.posZ).append(" / ")
        .append("Dimension: ")
        .append(DimensionManager.getProviderType(this.dim).getName().toUpperCase()).toString();
  }

  class NbtKey {
    public static final String POS_X = "PosX";
    public static final String POS_Y = "PosY";
    public static final String POS_Z = "PosZ";
    public static final String DIM = "Dim";
  }
}
