package com.syllient.livingchest.block;

import com.syllient.livingchest.tile.EyeBoneTile;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockReader;

public class EyeBoneBlock extends HorizontalBlock {
  public EyeBoneBlock() {
    super(AbstractBlock.Properties.of(Material.STONE));
    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
  }

  @Override
  public BlockState getStateForPlacement(final BlockItemUseContext ctx) {
    return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
  }

  @Override
  protected void createBlockStateDefinition(final Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }

  @Override
  public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
    return new EyeBoneTile();
  }

  @Override
  public BlockRenderType getRenderShape(final BlockState blockState) {
    return BlockRenderType.ENTITYBLOCK_ANIMATED;
  }
}
