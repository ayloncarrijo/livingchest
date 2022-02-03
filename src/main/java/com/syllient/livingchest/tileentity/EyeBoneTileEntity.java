package com.syllient.livingchest.tileentity;

import java.util.UUID;
import com.syllient.livingchest.animation.EyeBoneTileEntityAnimation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class EyeBoneTileEntity extends TileEntity implements IAnimatable {
  private final EyeBoneTileEntityAnimation animation = new EyeBoneTileEntityAnimation(this);
  private UUID ownerId = null;

  public EyeBoneTileEntity() {}

  @Override
  public NBTTagCompound writeToNBT(final NBTTagCompound compound) {
    if (this.ownerId != null) {
      compound.setUniqueId("OwnerId", this.ownerId);
    }

    return super.writeToNBT(compound);
  }

  @Override
  public void readFromNBT(final NBTTagCompound compound) {
    if (compound.hasUniqueId("OwnerId")) {
      this.ownerId = compound.getUniqueId("OwnerId");
    }

    super.readFromNBT(compound);
  }

  @Override
  public NBTTagCompound getUpdateTag() {
    return this.writeToNBT(new NBTTagCompound());
  }

  @Override
  public void handleUpdateTag(final NBTTagCompound compound) {
    this.readFromNBT(compound);
  }

  @Override
  public SPacketUpdateTileEntity getUpdatePacket() {
    return new SPacketUpdateTileEntity(this.getPos(), 1, this.writeToNBT(new NBTTagCompound()));
  }

  @Override
  public void onDataPacket(final NetworkManager net, final SPacketUpdateTileEntity pkt) {
    this.readFromNBT(pkt.getNbtCompound());
  }

  public UUID getOwnerId() {
    return this.ownerId;
  }

  public void setOwnerId(final UUID ownerId) {
    this.ownerId = ownerId;
    this.markDirty();
  }

  @Override
  public void registerControllers(final AnimationData data) {
    this.animation.registerControllers(data);
  }

  @Override
  public AnimationFactory getFactory() {
    return this.animation.getFactory();
  }
}
