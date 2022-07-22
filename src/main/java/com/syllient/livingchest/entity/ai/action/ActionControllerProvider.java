package com.syllient.livingchest.entity.ai.action;

import net.minecraft.entity.Entity;

public interface ActionControllerProvider<T extends Entity & ActionControllerProvider<T>> {
  Action[] getActions();

  ActionController<T> getActionController();

  void tickActionController();
}
