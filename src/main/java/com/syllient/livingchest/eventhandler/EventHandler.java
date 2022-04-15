package com.syllient.livingchest.eventhandler;

import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.PacketHandler;
import com.syllient.livingchest.network.message.SyncVirtualChesterMessage;
import com.syllient.livingchest.saveddata.VirtualChesterSavedData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber(modid = LivingChest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {
  @SubscribeEvent
  public static void handlePlayerLogIn(final PlayerEvent.PlayerLoggedInEvent event) {
    if (!event.getPlayer().level.isClientSide) {
      PacketHandler.INSTANCE.send(
          PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()),
          new SyncVirtualChesterMessage());
    }
  }

  @SubscribeEvent
  public static void handleServerTick(final TickEvent.ServerTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      VirtualChesterSavedData.getServerInstance(ServerLifecycleHooks.getCurrentServer().overworld())
          .tickServer();
    }
  }
}
