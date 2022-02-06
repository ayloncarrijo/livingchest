package com.syllient.livingchest.util;

import net.minecraftforge.common.DimensionManager;

public class Position {
  private double posX;
  private double posY;
  private double posZ;
  private int dim;

  public Position(final double posX, final double posY, final double posZ, final int dim) {
    this.posX = posX;
    this.posY = posY;
    this.posZ = posZ;
    this.dim = dim;
  }

  public double getX() {
    return this.posX;
  }

  public double getY() {
    return this.posY;
  }

  public double getZ() {
    return this.posZ;
  }

  public int getDim() {
    return this.dim;
  }

  public void setPos(final double posX, final double posY, final double posZ, final int dim) {
    this.posX = posX;
    this.posY = posY;
    this.posZ = posZ;
    this.dim = dim;
  }

  @Override
  public String toString() {
    return new StringBuilder().append("X: ").append((int) this.posX).append(" / ").append("Y: ")
        .append((int) this.posY).append(" / ").append("Z: ").append((int) this.posZ).append(" / ")
        .append("Dimension: ")
        .append(DimensionManager.getProviderType(this.dim).getName().toUpperCase()).toString();
  }
}
