package com.syllient.livingchest.entity.ai.action;

public class Action {
  private final int id;
  private final int delay;

  public Action(final int id, final int delay) {
    this.id = id;
    this.delay = delay;
  }

  public int getId() {
    return this.id;
  }

  public int getDelay() {
    return this.delay;
  }

  @Override
  public boolean equals(final Object obj) {
    return obj instanceof Action && this.getId() == ((Action) obj).getId();
  }
}
