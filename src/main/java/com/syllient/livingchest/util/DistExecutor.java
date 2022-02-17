package com.syllient.livingchest.util;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class DistExecutor {
  public static void runWhenOn(final Side side, final Runnable runnable) {
    if (side == FMLCommonHandler.instance().getSide()) {
      runnable.run();
    }
  }
}
