package com.syllient.livingchest.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumBlockRenderType;

public class EyeBoneBlock extends Block {
  public EyeBoneBlock() {
    super(Material.ROCK);
    this.setCreativeTab(CreativeTabs.MISC);
  }

  @Override
  public EnumBlockRenderType getRenderType(final IBlockState state) {
    return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
  }
}
