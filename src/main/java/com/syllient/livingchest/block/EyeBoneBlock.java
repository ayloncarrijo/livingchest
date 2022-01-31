package com.syllient.livingchest.block;

import java.util.List;

import com.syllient.livingchest.tileentity.EyeBoneTileEntity;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class EyeBoneBlock extends BlockHorizontal {
  private static final AxisAlignedBB AABB = new AxisAlignedBB(0.28D, 0.0D, 0.28D, 0.72D, 1.2D, 0.72D);

  public EyeBoneBlock() {
    super(Material.ROCK);
    this.setCreativeTab(CreativeTabs.MISC);
    this.setDefaultState(
        this.blockState.getBaseState().withProperty(BlockHorizontal.FACING, EnumFacing.NORTH));
  }

  @Override
  public void onBlockPlacedBy(
      final World worldIn,
      final BlockPos pos,
      final IBlockState state,
      final EntityLivingBase placer,
      final ItemStack stack) {
    final TileEntity tileEntity = worldIn.getTileEntity(pos);

    if (tileEntity instanceof EyeBoneTileEntity) {
      System.out.println(tileEntity);
    }
  }

  @Override
  public void addInformation(final ItemStack stack, final World player, final List<String> tooltip,
      final ITooltipFlag advanced) {
    tooltip.add("To spawn Chester, press Shift + Right Click on a block");
    super.addInformation(stack, player, tooltip, advanced);
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, BlockHorizontal.FACING);
  }

  @Override
  public IBlockState getStateFromMeta(final int meta) {
    return this.getDefaultState().withProperty(
        BlockHorizontal.FACING,
        EnumFacing.getHorizontal(meta));
  }

  @Override
  public int getMetaFromState(final IBlockState state) {
    return state.getValue(BlockHorizontal.FACING).getHorizontalIndex();
  }

  @Override
  public IBlockState getStateForPlacement(
      final World worldIn,
      final BlockPos pos,
      final EnumFacing facing,
      final float hitX,
      final float hitY,
      final float hitZ,
      final int meta,
      final EntityLivingBase placer) {
    return this.getDefaultState().withProperty(
        BlockHorizontal.FACING,
        placer.getHorizontalFacing().getOpposite());
  }

  @Override
  public boolean isOpaqueCube(final IBlockState state) {
    return false;
  }

  @Override
  public boolean isFullCube(final IBlockState state) {
    return false;
  }

  @Override
  public boolean isPassable(final IBlockAccess worldIn, final BlockPos pos) {
    return false;
  }

  @Override
  public boolean hasTileEntity(final IBlockState state) {
    return true;
  }

  @Override
  public TileEntity createTileEntity(final World world, final IBlockState state) {
    return new EyeBoneTileEntity();
  }

  @Override
  public AxisAlignedBB getBoundingBox(
      final IBlockState state,
      final IBlockAccess source,
      final BlockPos pos) {
    return EyeBoneBlock.AABB;
  }

  @Override
  public EnumBlockRenderType getRenderType(final IBlockState state) {
    return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
  }
}
