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

  public Position(final NBTTagCompound nbtCompoundIn) {
    this.deserializeNBT(nbtCompoundIn);
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
    final NBTTagCompound nbtCompound = new NBTTagCompound();

    nbtCompound.setDouble(NbtKey.POS_X, this.posX);
    nbtCompound.setDouble(NbtKey.POS_Y, this.posY);
    nbtCompound.setDouble(NbtKey.POS_Z, this.posZ);
    nbtCompound.setInteger(NbtKey.DIM, this.dim);

    return nbtCompound;
  }

  @Override
  public void deserializeNBT(final NBTTagCompound nbtCompoundIn) {
    this.posX = nbtCompoundIn.getDouble(NbtKey.POS_X);
    this.posY = nbtCompoundIn.getDouble(NbtKey.POS_Y);
    this.posZ = nbtCompoundIn.getDouble(NbtKey.POS_Z);
    this.dim = nbtCompoundIn.getInteger(NbtKey.DIM);
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
