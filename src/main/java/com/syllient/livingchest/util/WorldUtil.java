package com.syllient.livingchest.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class WorldUtil {
  public static Entity getEntityByUuid(final World world, final UUID uuid) {
    return world.loadedEntityList.stream().filter((entity) -> entity.getUniqueID().equals(uuid))
        .findFirst().orElse(null);
  }

  public static Entity getEntityByUuid(final UUID uuid) {
    return Arrays.asList(DimensionManager.getWorlds()).stream()
        .map((world) -> world.loadedEntityList).flatMap(Collection::stream)
        .filter((entity) -> entity.getUniqueID().equals(uuid)).findFirst().orElse(null);
  }
}
