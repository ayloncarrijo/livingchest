package com.syllient.livingchest.network.message;

import com.syllient.livingchest.saveddata.ChesterSavedData;
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

public class SyncChesterSavedDataMessage implements IMessage {
  private NBTTagCompound compound;

  public SyncChesterSavedDataMessage() {}

  @Override
  public void toBytes(final ByteBuf buf) {
    final World world = DimensionManager.getWorld(DimensionType.OVERWORLD.getId());
    ByteBufUtils.writeTag(buf, ChesterSavedData.get(world).getUpdateTag());
  }

  @Override
  public void fromBytes(final ByteBuf buf) {
    this.compound = ByteBufUtils.readTag(buf);
  }

  public static class Handler implements IMessageHandler<SyncChesterSavedDataMessage, IMessage> {
    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(final SyncChesterSavedDataMessage message, final MessageContext ctx) {
      Minecraft.getMinecraft().addScheduledTask(() -> {
        ChesterSavedData.get(Minecraft.getMinecraft().world).handleUpdateTag(message.compound);
      });

      return null;
    }
  }
}
