package com.syllient.livingchest;

import com.syllient.livingchest.network.message.SyncChesterSavedDataMessage;
import com.syllient.livingchest.saveddata.ChesterSavedData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = LivingChest.MOD_ID)
public class EventHandler {
  @SubscribeEvent
  public static void onPlayerLogIn(final PlayerLoggedInEvent event) {
    final EntityPlayer player = event.player;
    final World world = player.world;

    if (!world.isRemote) {
      PacketHandler.INSTANCE.sendTo(new SyncChesterSavedDataMessage(), (EntityPlayerMP) player);
    }
  }

  @SubscribeEvent
  public static void onServerTick(final TickEvent.ServerTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      final World world = DimensionManager.getWorld(DimensionType.OVERWORLD.getId());
      ChesterSavedData.get(world).onServerTick();
    }
  }
}
