package com.syllient.livingchest.item;

import com.syllient.livingchest.eventhandler.registry.BlockRegistry;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class EyeBoneItem extends BlockItem {
  public EyeBoneItem() {
    super(BlockRegistry.EYE_BONE, new Item.Properties().tab(ItemGroup.TAB_MISC).stacksTo(1));
  }
}
