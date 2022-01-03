package com.syllient.livingchest;

import com.syllient.livingchest.client.gui.GuiScreenChester;
import com.syllient.livingchest.entity.EntityChester;
import com.syllient.livingchest.inventory.ContainerChester;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
  public static final int ID_GUI_CHESTER = 0;

  @Override
  public Object getServerGuiElement(
      final int ID,
      final EntityPlayer player,
      final World world,
      final int x,
      final int y,
      final int z) {
    if (ID == ID_GUI_CHESTER) {
      final Entity entity = world.getEntityByID(x);

      if (entity instanceof EntityChester) {
        return new ContainerChester(
            player,
            (EntityChester) entity);
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
    if (ID == ID_GUI_CHESTER) {
      final Entity entity = world.getEntityByID(x);

      if (entity instanceof EntityChester) {
        return new GuiScreenChester((EntityChester) entity);
      }
    }

    return null;
  }
}
