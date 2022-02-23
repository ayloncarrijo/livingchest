package com.syllient.livingchest.eventhandler.registry;

import java.util.function.Supplier;
import com.syllient.livingchest.LivingChest;
import com.syllient.livingchest.tile.EyeBoneTile;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = LivingChest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(LivingChest.MOD_ID)
public class TileRegistry {
  public static final TileEntityType<EyeBoneTile> EYE_BONE = null;

  @SubscribeEvent
  public static void initialize(final RegistryEvent.Register<TileEntityType<?>> event) {
    event.getRegistry()
        .registerAll(createEntry("eye_bone", EyeBoneTile::new, BlockRegistry.EYE_BONE));

  }

  private static <T extends TileEntity> TileEntityType<?> createEntry(final String name,
      final Supplier<T> supplier, final Block... blocks) {
    return TileEntityType.Builder.of(supplier, blocks).build(null).setRegistryName(name);
  }
}
