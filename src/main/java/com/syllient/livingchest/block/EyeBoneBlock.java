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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class EyeBoneBlock extends HorizontalBlock {
  private static class Shape {
    private static final VoxelShape EYE =
        VoxelShapes.box(0.345D, 0.535D, 0.345D, 0.655D, 0.84D, 0.655D);

    private static final VoxelShape GRIP =
        VoxelShapes.box(0.45D, 0.1D, 0.45D, 0.55D, 0.535D, 0.55D);

    private static final VoxelShape BASE_NS =
        VoxelShapes.box(0.39D, 0.0D, 0.45D, 0.61D, 0.1D, 0.55D);

    private static final VoxelShape BASE_WE =
        VoxelShapes.box(0.45D, 0.0D, 0.39D, 0.55D, 0.1D, 0.61D);

    private static final VoxelShape FULL_NS = VoxelShapes.or(EYE, GRIP, BASE_NS);

    private static final VoxelShape FULL_WE = VoxelShapes.or(EYE, GRIP, BASE_WE);
  }

  public EyeBoneBlock() {
    super(AbstractBlock.Properties.of(Material.WOOD));
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
  public VoxelShape getShape(final BlockState state, final IBlockReader reader, final BlockPos pos,
      final ISelectionContext ctx) {
    final Direction direction = state.getValue(FACING);

    return direction == Direction.WEST || direction == Direction.EAST ? Shape.FULL_WE
        : Shape.FULL_NS;
  }

  @Override
  public BlockRenderType getRenderShape(final BlockState blockState) {
    return BlockRenderType.ENTITYBLOCK_ANIMATED;
  }
}
