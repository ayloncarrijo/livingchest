package com.syllient.livingchest.eventhandler.server;

import com.syllient.livingchest.LivingChest;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LivingChest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE,
    value = Dist.DEDICATED_SERVER)
public class ServerEventHandler {

}
