package com.syllient.livingchest.util;

public class Position {
  private double posX;
  private double posY;
  private double posZ;
  private int dim;

  public Position() {
    this.posX = 0;
    this.posY = 0;
    this.posZ = 0;
    this.dim = 0;
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
}
