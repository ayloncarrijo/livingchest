package com.syllient.livingchest.network.message;

import com.syllient.livingchest.saveddata.VirtualChesterSavedData;
import com.syllient.livingchest.util.DistExecutor;
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

public class SyncVirtualChesterMessage implements IMessage {
  private NBTTagCompound tagCompound;

  public SyncVirtualChesterMessage() {}

  @Override
  public void toBytes(final ByteBuf buf) {
    final World world = DimensionManager.getWorld(DimensionType.OVERWORLD.getId());
    ByteBufUtils.writeTag(buf,
        VirtualChesterSavedData.getInstance(world).writeToNBT(new NBTTagCompound()));
  }

  @Override
  public void fromBytes(final ByteBuf buf) {
    this.tagCompound = ByteBufUtils.readTag(buf);
  }

  public static class Handler implements IMessageHandler<SyncVirtualChesterMessage, IMessage> {
    @Override
    public IMessage onMessage(final SyncVirtualChesterMessage message, final MessageContext ctx) {
      DistExecutor.runWhenOn(Side.CLIENT, new Runnable() {
        @Override
        public void run() {
          Minecraft.getMinecraft().addScheduledTask(() -> {
            VirtualChesterSavedData.getInstance(Minecraft.getMinecraft().world)
                .readFromNBT(message.tagCompound);
          });
        }
      });

      return null;
    }

  }
}
