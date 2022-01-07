package com.syllient.livingchest;

import com.syllient.livingchest.client.gui.ChesterGui;
import com.syllient.livingchest.container.ChesterContainer;
import com.syllient.livingchest.entity.ChesterEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
  public static class Gui {
    public static final int CHESTER = 0;
  }

  @Override
  public Object getServerGuiElement(
      final int ID,
      final EntityPlayer player,
      final World world,
      final int x,
      final int y,
      final int z) {
    if (ID == Gui.CHESTER) {
      final Entity entity = world.getEntityByID(x);

      if (entity instanceof ChesterEntity) {
        return new ChesterContainer(
            player,
            (ChesterEntity) entity);
      }
    }

    return null;
  }

  @Override
  public Object getClientGuiElement(
      final int ID,
      final EntityPlayer player,
      final World world,
      final int x,
      final int y,
      final int z) {
    if (ID == Gui.CHESTER) {
      final Entity entity = world.getEntityByID(x);

      if (entity instanceof ChesterEntity) {
        return new ChesterGui((ChesterEntity) entity);
      }
    }

    return null;
  }
}
