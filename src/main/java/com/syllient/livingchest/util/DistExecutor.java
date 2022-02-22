package com.syllient.livingchest.util;

import java.util.function.Supplier;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class DistExecutor {
  public static void runWhenOn(final Side side, final Supplier<Runnable> supplier) {
    if (side == FMLCommonHandler.instance().getSide()) {
      supplier.get().run();
    }
  }
}
