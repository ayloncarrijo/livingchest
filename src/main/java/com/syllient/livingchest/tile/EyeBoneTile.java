package com.syllient.livingchest.tile;

import java.util.UUID;
import com.syllient.livingchest.animation.block.EyeBoneBlockAnimation;
import com.syllient.livingchest.eventhandler.registry.TileRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class EyeBoneTile extends TileEntity implements IAnimatable {
  private final EyeBoneBlockAnimation animation = new EyeBoneBlockAnimation(this);
  private UUID ownerId = null;
  public boolean isClosed = false;

  public EyeBoneTile() {
    super(TileRegistry.EYE_BONE);
  }

  @Override
  public CompoundNBT save(final CompoundNBT compoundIn) {
    if (this.ownerId != null) {
      compoundIn.putUUID(NbtKey.OWNER_ID, this.ownerId);
    }

    return super.save(compoundIn);
  }

  @Override
  public void load(final BlockState state, final CompoundNBT compoundIn) {
    if (compoundIn.contains(NbtKey.OWNER_ID)) {
      this.ownerId = compoundIn.getUUID(NbtKey.OWNER_ID);
    }

    super.load(state, compoundIn);
  }

  @Override
  public CompoundNBT getUpdateTag() {
    return this.save(new CompoundNBT());
  }

  @Override
  public void handleUpdateTag(final BlockState state, final CompoundNBT compoundIn) {
    this.load(state, compoundIn);
  }

  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    return new SUpdateTileEntityPacket(this.getBlockPos(), 1, this.save(new CompoundNBT()));
  }

  @Override
  public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt) {
    this.load(this.getBlockState(), pkt.getTag());
  }

  public UUID getOwnerId() {
    return this.ownerId;
  }

  public void setOwnerId(final UUID ownerId) {
    this.ownerId = ownerId;
    this.setChanged();
  }

  @Override
  public void registerControllers(final AnimationData data) {
    this.animation.registerControllers(data);
  }

  @Override
  public AnimationFactory getFactory() {
    return this.animation.getFactory();
  }

  class NbtKey {
    public static final String OWNER_ID = "OwnerId";
  }
}
