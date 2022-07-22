package com.syllient.livingchest.entity.ai.action;

import com.syllient.livingchest.PacketHandler;
import com.syllient.livingchest.network.message.ActionMessage;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.network.PacketDistributor;

public class ActionController<T extends Entity & ActionControllerProvider<T>> {
  private Action action = null;

  private int delay = 0;

  private boolean shouldStop = false;

  private final T entity;

  public ActionController(final T entity) {
    this.entity = entity;
  }

  public void tick() {
    if (this.delay > 0) {
      --this.delay;
    }
  }

  public boolean canRun() {
    return this.action == null && this.delay == 0;
  }

  public void markToStop() {
    this.shouldStop = true;
  }

  public boolean shouldStop() {
    return this.shouldStop;
  }

  public void startAction(final Action action) {
    this.setAction(action);
  }

  public void stopAction() {
    this.shouldStop = false;
    this.delay = this.action.getDelay();
    this.setAction(null);
  }

  public Action getAction() {
    return this.action;
  }

  public void setAction(final Action action) {
    this.action = action;
    this.syncAction();
  }

  public void setActionWithoutSync(final Action action) {
    this.action = action;
  }

  public void syncAction() {
    PacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> this.entity),
        ActionMessage.create(this.entity, this.action));
  }
}
