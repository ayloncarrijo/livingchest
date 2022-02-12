package com.syllient.livingchest.entity.ai;

import com.syllient.livingchest.entity.ChesterEntity;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.util.math.BlockPos;

public class ChesterSitEntityAi extends EntityAISit {
  private final ChesterEntity chester;

  public ChesterSitEntityAi(final ChesterEntity chester) {
    super(chester);
    this.chester = chester;
  }

  @Override
  public boolean shouldExecute() {
    return this.chester.getEyeBone() != null;
  }

  @Override
  public void updateTask() {
    final BlockPos eyeBone = this.chester.getEyeBone();

    // TODO: verificar logica
    if (eyeBone != null && this.chester.getDistanceSq(eyeBone) > 64) {
      this.chester.attemptTeleport(eyeBone.getX() + 0.5, eyeBone.getY() + 1, eyeBone.getZ() + 0.5);
    }
  }
}
