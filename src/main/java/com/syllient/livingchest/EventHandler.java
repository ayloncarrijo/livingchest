package com.syllient.livingchest;

import com.syllient.livingchest.network.message.SyncVirtualChesterMessage;
import com.syllient.livingchest.saveddata.VirtualChesterSavedData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = LivingChest.MOD_ID)
public class EventHandler {
  @SubscribeEvent
  public static void onPlayerChangeDimension(final PlayerEvent.PlayerChangedDimensionEvent event) {
    if (!event.player.world.isRemote) {
      PacketHandler.INSTANCE.sendTo(new SyncVirtualChesterMessage(), (EntityPlayerMP) event.player);
    }
  }

  @SubscribeEvent
  public static void onPlayerLogIn(final PlayerEvent.PlayerLoggedInEvent event) {
    if (!event.player.world.isRemote) {
      PacketHandler.INSTANCE.sendTo(new SyncVirtualChesterMessage(), (EntityPlayerMP) event.player);
    }
  }

  @SubscribeEvent
  public static void onServerTick(final TickEvent.ServerTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      final World world = DimensionManager.getWorld(DimensionType.OVERWORLD.getId());
      VirtualChesterSavedData.getInstance(world).onServerTick();
    }
  }
}
