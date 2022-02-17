package com.syllient.livingchest.block;

import java.util.List;
import com.syllient.livingchest.saveddata.VirtualChesterSavedData;
import com.syllient.livingchest.tile.EyeBoneTile;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class EyeBoneBlock extends BlockHorizontal {
  private static final AxisAlignedBB AABB =
      new AxisAlignedBB(0.345D, 0.0D, 0.345D, 0.655D, 0.84D, 0.655D);

  public EyeBoneBlock() {
    super(Material.ROCK);
    this.setCreativeTab(CreativeTabs.MISC);
    this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
  }

  @Override
  public void onBlockPlacedBy(final World worldIn, final BlockPos pos, final IBlockState state,
      final EntityLivingBase placer, final ItemStack stack) {
    final TileEntity tileEntity = worldIn.getTileEntity(pos);

    if (tileEntity instanceof EyeBoneTile && placer instanceof EntityPlayer) {
      if (!worldIn.isRemote) {
        VirtualChesterSavedData.getInstance(worldIn).onPlaceEyeBone((EntityPlayer) placer, pos);
      }

      ((EyeBoneTile) tileEntity).setOwnerId(placer.getUniqueID());
    }
  }

  @Override
  public boolean canHarvestBlock(final IBlockAccess world, final BlockPos pos,
      final EntityPlayer player) {
    return true;
  }

  @Override
  public void addInformation(final ItemStack stack, final World player, final List<String> tooltip,
      final ITooltipFlag advanced) {
    tooltip.add("To spawn/despawn your Chester, use the Eye Bone on a block while sneaking.");
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, FACING);
  }

  @Override
  public IBlockState getStateFromMeta(final int meta) {
    return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
  }

  @Override
  public int getMetaFromState(final IBlockState state) {
    return state.getValue(FACING).getHorizontalIndex();
  }

  @Override
  public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos,
      final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta,
      final EntityLivingBase placer) {
    return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
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
    return new EyeBoneTile();
  }

  @Override
  public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source,
      final BlockPos pos) {
    return AABB;
  }

  @Override
  public EnumBlockRenderType getRenderType(final IBlockState state) {
    return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
  }
}
