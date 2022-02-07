package com.syllient.livingchest.network.message;

import com.syllient.livingchest.saveddata.WorldChesterSavedData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SyncWorldChesterSavedDataMessage implements IMessage {
  private NBTTagCompound nbtData;

  public SyncWorldChesterSavedDataMessage() {}

  @Override
  public void toBytes(final ByteBuf buf) {
    final World world = DimensionManager.getWorld(DimensionType.OVERWORLD.getId());
    ByteBufUtils.writeTag(buf,
        WorldChesterSavedData.getInstance(world).writeToNBT(new NBTTagCompound()));
  }

  @Override
  public void fromBytes(final ByteBuf buf) {
    this.nbtData = ByteBufUtils.readTag(buf);
  }

  public static class Handler
      implements IMessageHandler<SyncWorldChesterSavedDataMessage, IMessage> {
    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(final SyncWorldChesterSavedDataMessage message,
        final MessageContext ctx) {
      Minecraft.getMinecraft().addScheduledTask(() -> {
        WorldChesterSavedData.getInstance(Minecraft.getMinecraft().world)
            .readFromNBT(message.nbtData);
      });

      return null;
    }
  }
}
