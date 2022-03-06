package com.syllient.livingchest.network.message;

import java.util.function.Supplier;
import com.syllient.livingchest.saveddata.VirtualChesterSavedData;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class SyncVirtualChesterMessage {
  private CompoundNBT compound;

  public SyncVirtualChesterMessage() {}

  public static void encode(final SyncVirtualChesterMessage message, final PacketBuffer buffer) {
    buffer.writeNbt(VirtualChesterSavedData
        .getServerInstance(ServerLifecycleHooks.getCurrentServer().overworld())
        .save(new CompoundNBT()));
  }

  public static SyncVirtualChesterMessage decode(final PacketBuffer buffer) {
    final SyncVirtualChesterMessage message = new SyncVirtualChesterMessage();
    message.compound = buffer.readNbt();
    return message;
  }

  public static void handle(final SyncVirtualChesterMessage message,
      final Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> new SafeRunnable() {
        @Override
        public void run() {
          final Minecraft minecraft = Minecraft.getInstance();
          VirtualChesterSavedData.getClientInstance(minecraft.level).load(message.compound);
        }
      });
    });

    ctx.get().setPacketHandled(true);
  }
}
