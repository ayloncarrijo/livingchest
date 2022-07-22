package com.syllient.livingchest.entity.ai.action;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;

public abstract class ActionGoal<T extends Entity & ActionControllerProvider<T>> extends Goal {
  private final Action action;

  private final T entity;

  public ActionGoal(final T entity, final Action action) {
    this.action = action;
    this.entity = entity;
  }

  public abstract boolean shouldRun();

  public boolean shouldContinueToRun() {
    return this.shouldRun();
  }

  @Override
  public boolean canUse() {
    final boolean shouldRun = this.shouldRun();

    if (shouldRun && this.entity.getActionController().getAction() != null) {
      this.entity.getActionController().markToStop();
    }

    return shouldRun && this.entity.getActionController().canRun();
  }

  @Override
  public boolean canContinueToUse() {
    return this.shouldContinueToRun() && !this.entity.getActionController().shouldStop();
  }

  @Override
  public void start() {
    this.entity.getActionController().startAction(this.action);
  }

  @Override
  public void stop() {
    this.entity.getActionController().stopAction();
  }
}
