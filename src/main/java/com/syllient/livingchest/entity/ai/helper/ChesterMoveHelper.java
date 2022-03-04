package com.syllient.livingchest.entity.ai.helper;

import com.syllient.livingchest.entity.ChesterEntity;
import net.minecraft.entity.ai.EntityMoveHelper;

public class ChesterMoveHelper extends EntityMoveHelper {
  private final ChesterEntity chester;

  public ChesterMoveHelper(final ChesterEntity chester) {
    super(chester);
    this.chester = chester;
  }

  @Override
  public void onUpdateMoveHelper() {
    if (this.chester.isMouthOpen()) {
      this.chester.setMoveForward(0.0F);
      return;
    }

    super.onUpdateMoveHelper();
  }

  @Override
  protected float limitAngle(final float sourceAngle, final float targetAngle,
      final float maximumChange) {
    return super.limitAngle(sourceAngle, targetAngle, 30.0F);
  }
}
