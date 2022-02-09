package com.syllient.livingchest.entity.ai;

import com.syllient.livingchest.entity.ChesterEntity;
import net.minecraft.entity.ai.EntityAISit;

public class ChesterSitEntityAi extends EntityAISit {
  private final ChesterEntity chester;

  public ChesterSitEntityAi(final ChesterEntity chester) {
    super(chester);
    this.chester = chester;
  }

  @Override
  public boolean shouldExecute() {
    return this.chester.isTamed() && this.chester.isSitting();
  }
}
