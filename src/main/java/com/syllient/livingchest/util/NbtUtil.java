package com.syllient.livingchest.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import org.apache.logging.log4j.util.TriConsumer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NbtUtil {
  public static <K, V> NBTTagList serializeMap(final Map<K, V> map,
      final TriConsumer<NBTTagCompound, String, K> keySetter,
      final TriConsumer<NBTTagCompound, String, V> valueSetter) {
    final NBTTagList list = new NBTTagList();

    map.forEach((key, value) -> {
      final NBTTagCompound compound = new NBTTagCompound();
      keySetter.accept(compound, NbtKey.KEY, key);
      valueSetter.accept(compound, NbtKey.VALUE, value);
      list.appendTag(compound);
    });

    return list;
  }

  public static <K, V> HashMap<K, V> deserializeMap(final NBTTagList list,
      final BiFunction<NBTTagCompound, String, K> keyGetter,
      final BiFunction<NBTTagCompound, String, V> valueGetter) {
    final HashMap<K, V> map = new HashMap<>();

    list.forEach((nbt) -> {
      final NBTTagCompound compound = (NBTTagCompound) nbt;
      map.put(keyGetter.apply(compound, NbtKey.KEY), valueGetter.apply(compound, NbtKey.VALUE));
    });

    return map;
  }

  class NbtKey {
    public static final String KEY = "Key";
    public static final String VALUE = "Value";
  }
}
