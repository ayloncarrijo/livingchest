package com.syllient.livingchest.entity.ai.helper;

import com.syllient.livingchest.entity.ChesterEntity;
import net.minecraft.entity.ai.controller.MovementController;

public class ChesterMoveHelper extends MovementController {
  private final ChesterEntity chester;

  public ChesterMoveHelper(final ChesterEntity chester) {
    super(chester);
    this.chester = chester;
  }

  @Override
  public void tick() {
    if (this.chester.getTicksUntilActionEnd() > 0) {
      this.chester.setZza(0.0F);
      this.chester.setIsMoving(false);
      return;
    }

    super.tick();
    this.chester.setIsMoving(this.chester.zza > 0.0F);
    this.chester.setYawRotations(this.mob.yRot);
  }

  @Override
  protected float rotlerp(final float sourceAngle, final float targetAngle,
      final float maximumChange) {
    return super.rotlerp(sourceAngle, targetAngle, 30.0F);
  }
}
