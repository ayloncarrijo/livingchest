package com.syllient.livingchest.entity.ai.pathfinding;

import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

public class FixedGroundPathNavigator extends GroundPathNavigator {
  public FixedGroundPathNavigator(final MobEntity mob, final World world) {
    super(mob, world);
  }

  @Override
  protected void followThePath() {
    final Vector3d vector3d = this.getTempMobPos();
    this.maxDistanceToWaypoint = this.mob.getBbWidth() > 0.75F ? this.mob.getBbWidth() / 2.0F
        : 0.75F - this.mob.getBbWidth() / 2.0F;
    final Vector3i vector3i = this.path.getNextNodePos();
    final double d0 =
        Math.abs(this.mob.getX() - ((double) vector3i.getX() + (this.mob.getBbWidth() + 1) / 2D));
    final double d1 = Math.abs(this.mob.getY() - (double) vector3i.getY());
    final double d2 =
        Math.abs(this.mob.getZ() - ((double) vector3i.getZ() + (this.mob.getBbWidth() + 1) / 2D));
    final boolean flag = d0 <= (double) this.maxDistanceToWaypoint
        && d2 <= (double) this.maxDistanceToWaypoint && d1 < 1.0D;
    if (flag || this.mob.canCutCorner(this.path.getNextNode().type)
        && this.shouldTargetNextNodeInDirection(vector3d)) {
      this.path.advance();
    }

    // === Fix: 1.16 movement. Copied from 1.12.2 === //

    int i = this.path.getNodeCount();

    for (int j = this.path.getNextNodeIndex(); j < this.path.getNodeCount(); ++j) {
      if ((double) this.path.getNode(j).y != Math.floor(vector3d.y)) {
        i = j;
        break;
      }
    }

    final int k = MathHelper.ceil(this.mob.getBbWidth());
    final int l = MathHelper.ceil(this.mob.getBbHeight());
    final int i1 = k;

    for (int j1 = i - 1; j1 >= this.path.getNextNodeIndex(); --j1) {
      if (this.canMoveDirectly(vector3d, this.path.getEntityPosAtNode(this.mob, j1), k, l, i1)) {
        this.path.setNextNodeIndex(j1);
        break;
      }
    }

    // === Fix: 1.16 movement. Copied from 1.12.2 === //

    this.doStuckDetection(vector3d);
  }

  private boolean shouldTargetNextNodeInDirection(final Vector3d vector) {
    if (this.path.getNextNodeIndex() + 1 >= this.path.getNodeCount()) {
      return false;
    } else {
      final Vector3d vector3d = Vector3d.atBottomCenterOf(this.path.getNextNodePos());
      if (!vector.closerThan(vector3d, 2.0D)) {
        return false;
      } else {
        final Vector3d vector3d1 =
            Vector3d.atBottomCenterOf(this.path.getNodePos(this.path.getNextNodeIndex() + 1));
        final Vector3d vector3d2 = vector3d1.subtract(vector3d);
        final Vector3d vector3d3 = vector.subtract(vector3d);
        return vector3d2.dot(vector3d3) > 0.0D;
      }
    }
  }
}
