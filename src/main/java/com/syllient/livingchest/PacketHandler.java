package com.syllient.livingchest;

import com.syllient.livingchest.network.message.SyncWorldChesterSavedDataMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
  public static final SimpleNetworkWrapper INSTANCE =
      NetworkRegistry.INSTANCE.newSimpleChannel(LivingChest.MOD_ID);

  public static void initialize() {
    int id = 0;

    INSTANCE.registerMessage(SyncWorldChesterSavedDataMessage.Handler.class,
        SyncWorldChesterSavedDataMessage.class, id++, Side.CLIENT);
  }
}
