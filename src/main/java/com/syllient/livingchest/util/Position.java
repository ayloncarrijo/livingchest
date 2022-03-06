package com.syllient.livingchest.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class Position implements INBTSerializable<CompoundNBT> {
  private double posX;
  private double posY;
  private double posZ;
  private String dim;

  public Position(final double posX, final double posY, final double posZ, final String dim) {
    this.setPosition(posX, posY, posZ, dim);
  }

  public Position(final CompoundNBT tagCompoundIn) {
    this.deserializeNBT(tagCompoundIn);
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

  public String getDim() {
    return this.dim;
  }

  public void setPosition(final double posX, final double posY, final double posZ,
      final String dim) {
    this.posX = posX;
    this.posY = posY;
    this.posZ = posZ;
    this.dim = dim;
  }

  @Override
  public CompoundNBT serializeNBT() {
    final CompoundNBT tagCompound = new CompoundNBT();

    tagCompound.putDouble(TagKey.POS_X, this.posX);
    tagCompound.putDouble(TagKey.POS_Y, this.posY);
    tagCompound.putDouble(TagKey.POS_Z, this.posZ);
    tagCompound.putString(TagKey.DIM, this.dim);

    return tagCompound;
  }

  @Override
  public void deserializeNBT(final CompoundNBT tagCompoundIn) {
    this.posX = tagCompoundIn.getDouble(TagKey.POS_X);
    this.posY = tagCompoundIn.getDouble(TagKey.POS_Y);
    this.posZ = tagCompoundIn.getDouble(TagKey.POS_Z);
    this.dim = tagCompoundIn.getString(TagKey.DIM);
  }

  @Override
  public String toString() {
    return new StringBuilder().append("X: ").append((int) this.posX).append(" / ").append("Y: ")
        .append((int) this.posY).append(" / ").append("Z: ").append((int) this.posZ).append(" / ")
        .append("Dimension: ").append(this.dim.toUpperCase()).toString();
  }

  class TagKey {
    public static final String POS_X = "PosX";
    public static final String POS_Y = "PosY";
    public static final String POS_Z = "PosZ";
    public static final String DIM = "Dim";
  }
}
