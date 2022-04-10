package com.syllient.livingchest.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import org.apache.logging.log4j.util.TriConsumer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class NbtUtil {
  public static <K, V> ListNBT serializeMap(final Map<K, V> map,
      final TriConsumer<CompoundNBT, String, K> keySetter,
      final TriConsumer<CompoundNBT, String, V> valueSetter) {
    final ListNBT list = new ListNBT();

    map.forEach((key, value) -> {
      final CompoundNBT compound = new CompoundNBT();
      keySetter.accept(compound, NbtKey.KEY, key);
      valueSetter.accept(compound, NbtKey.VALUE, value);
      list.add(compound);
    });

    return list;
  }

  public static <K, V> HashMap<K, V> deserializeMap(final ListNBT list,
      final BiFunction<CompoundNBT, String, K> keyGetter,
      final BiFunction<CompoundNBT, String, V> valueGetter) {
    final HashMap<K, V> map = new HashMap<>();

    list.forEach((nbt) -> {
      final CompoundNBT compound = (CompoundNBT) nbt;
      map.put(keyGetter.apply(compound, NbtKey.KEY), valueGetter.apply(compound, NbtKey.VALUE));
    });

    return map;
  }

  class NbtKey {
    public static final String KEY = "Key";
    public static final String VALUE = "Value";
  }
}
