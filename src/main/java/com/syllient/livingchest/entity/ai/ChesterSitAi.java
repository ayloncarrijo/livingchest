package com.syllient.livingchest.entity.ai;

import com.syllient.livingchest.entity.ChesterEntity;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.util.math.BlockPos;

public class ChesterSitAi extends SitGoal {
  private final ChesterEntity chester;

  public ChesterSitAi(final ChesterEntity chester) {
    super(chester);
    this.chester = chester;
  }

  @Override
  public boolean canUse() {
    return this.chester.getEyeBone() != null;
  }

  @Override
  public void tick() {
    final BlockPos eyeBone = this.chester.getEyeBone();

    if (eyeBone != null && !eyeBone.closerThan(this.chester.position(), 8)) {
      this.chester.randomTeleport(eyeBone.getX() + 0.5, eyeBone.getY() + 1, eyeBone.getZ() + 0.5,
          false);
    }
  }
}
