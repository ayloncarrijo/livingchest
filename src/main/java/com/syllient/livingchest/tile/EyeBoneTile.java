package com.syllient.livingchest.tile;

import java.util.UUID;
import com.syllient.livingchest.animation.block.EyeBoneBlockAnimation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class EyeBoneTile extends TileEntity implements IAnimatable {
  private final EyeBoneBlockAnimation animation = new EyeBoneBlockAnimation(this);
  private UUID ownerId = null;
  public boolean isClosed = false;

  public EyeBoneTile() {}

  @Override
  public NBTTagCompound writeToNBT(final NBTTagCompound compoundIn) {
    if (this.ownerId != null) {
      compoundIn.setUniqueId(NbtKey.OWNER_ID, this.ownerId);
    }

    return super.writeToNBT(compoundIn);
  }

  @Override
  public void readFromNBT(final NBTTagCompound compoundIn) {
    if (compoundIn.hasUniqueId(NbtKey.OWNER_ID)) {
      this.ownerId = compoundIn.getUniqueId(NbtKey.OWNER_ID);
    }

    super.readFromNBT(compoundIn);
  }

  @Override
  public NBTTagCompound getUpdateTag() {
    return this.writeToNBT(new NBTTagCompound());
  }

  @Override
  public void handleUpdateTag(final NBTTagCompound tagCompoundIn) {
    this.readFromNBT(tagCompoundIn);
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

  class NbtKey {
    public static final String OWNER_ID = "OwnerId";
  }
}
