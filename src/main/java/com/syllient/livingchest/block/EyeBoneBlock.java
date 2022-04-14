package com.syllient.livingchest.block;

import java.util.List;
import com.syllient.livingchest.saveddata.VirtualChesterSavedData;
import com.syllient.livingchest.tile.EyeBoneTile;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class EyeBoneBlock extends HorizontalBlock {
  private static class Shape {
    private static final VoxelShape EYE =
        VoxelShapes.box(0.3425D, 0.5375D, 0.3425D, 0.6575D, 0.85D, 0.6575D);

    private static final VoxelShape GRIP =
        VoxelShapes.box(0.455D, 0.09125D, 0.455D, 0.545D, 0.545D, 0.545D);

    private static final VoxelShape BASE_NS =
        VoxelShapes.box(0.3875D, 0.0D, 0.455D, 0.6125D, 0.09125D, 0.545D);

    private static final VoxelShape BASE_WE =
        VoxelShapes.box(0.455D, 0.0D, 0.3875D, 0.545D, 0.09125D, 0.6125D);

    private static final VoxelShape FULL_NS = VoxelShapes.or(EYE, GRIP, BASE_NS);

    private static final VoxelShape FULL_WE = VoxelShapes.or(EYE, GRIP, BASE_WE);
  }

  public EyeBoneBlock() {
    super(AbstractBlock.Properties.of(Material.WOOD));
    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
  }

  @Override
  public void setPlacedBy(final World world, final BlockPos pos, final BlockState state,
      final LivingEntity placer, final ItemStack stack) {
    final TileEntity tileEntity = world.getBlockEntity(pos);

    if (tileEntity instanceof EyeBoneTile && placer instanceof PlayerEntity) {
      if (!world.isClientSide) {
        VirtualChesterSavedData.getServerInstance(world)
            .handleEyeBonePlacement((PlayerEntity) placer, pos);
      }

      ((EyeBoneTile) tileEntity).setOwnerId(placer.getUUID());
    }
  }

  @Override
  public void appendHoverText(final ItemStack stack, final IBlockReader reader,
      final List<ITextComponent> tooltip, final ITooltipFlag flag) {
    tooltip.add(new StringTextComponent(TextFormatting.GRAY
        + "To spawn/despawn your Chester, use the Eye Bone on a block while sneaking."));
  }

  @Override
  protected void createBlockStateDefinition(final Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Override
  public BlockState getStateForPlacement(final BlockItemUseContext ctx) {
    return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
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
    final Direction facing = state.getValue(FACING);

    return facing == Direction.WEST || facing == Direction.EAST ? Shape.FULL_WE : Shape.FULL_NS;
  }

  @Override
  public BlockRenderType getRenderShape(final BlockState blockState) {
    return BlockRenderType.ENTITYBLOCK_ANIMATED;
  }
}
