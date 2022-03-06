package com.syllient.livingchest;

import com.syllient.livingchest.network.message.SyncVirtualChesterMessage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
  private static final String PROTOCOL_VERSION = "1";

  public static final SimpleChannel INSTANCE =
      NetworkRegistry.newSimpleChannel(new ResourceLocation(LivingChest.MOD_ID, "main"),
          () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

  public static void initialize() {
    int id = 0;

    INSTANCE.registerMessage(id++, SyncVirtualChesterMessage.class,
        SyncVirtualChesterMessage::encode, SyncVirtualChesterMessage::decode,
        SyncVirtualChesterMessage::handle);
  }
}
