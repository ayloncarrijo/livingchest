package com.syllient.livingchest.util;

public class MutableInt {
  private int value;

  public MutableInt(final int value) {
    this.value = value;
  }

  public int getInt() {
    return this.value;
  }

  public int increment() {
    return ++this.value;
  }

  public int increment(final int value) {
    this.value += value;
    return this.value;
  }

  public int decrement() {
    return --this.value;
  }

  public int decrement(final int value) {
    this.value -= value;
    return this.value;
  }
}
