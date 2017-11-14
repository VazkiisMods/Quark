package vazkii.quark.base.block;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockObserver;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMetaVariants.EnumBase;
import vazkii.arl.block.BlockMod;
import vazkii.arl.interf.IBlockColorProvider;

// Mostly BlockRedstoneWire copypasta
public abstract class BlockQuarkDust extends BlockMod implements IQuarkBlock, IBlockColorProvider {

	public static final PropertyEnum<EnumAttachPosition> NORTH = PropertyEnum.<EnumAttachPosition>create("north", EnumAttachPosition.class);
	public static final PropertyEnum<EnumAttachPosition> EAST = PropertyEnum.<EnumAttachPosition>create("east", EnumAttachPosition.class);
	public static final PropertyEnum<EnumAttachPosition> SOUTH = PropertyEnum.<EnumAttachPosition>create("south", EnumAttachPosition.class);
	public static final PropertyEnum<EnumAttachPosition> WEST = PropertyEnum.<EnumAttachPosition>create("west", EnumAttachPosition.class);	

	protected static final AxisAlignedBB[] WIRE_AABB = new AxisAlignedBB[] {
			new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D), 
			new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 1.0D), 
			new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D), 
			new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 1.0D), 
			new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 0.8125D, 0.0625D, 0.8125D), 
			new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 0.8125D, 0.0625D, 1.0D), 
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.8125D, 0.0625D, 0.8125D), 
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.8125D, 0.0625D, 1.0D), 
			new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 1.0D, 0.0625D, 0.8125D), 
			new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 1.0D, 0.0625D, 1.0D), 
			new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 1.0D, 0.0625D, 0.8125D), 
			new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 1.0D, 0.0625D, 1.0D), 
			new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 1.0D, 0.0625D, 0.8125D), 
			new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D), 
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 0.8125D), 
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D) };

	public BlockQuarkDust(String name) {
		super(name, Material.CIRCUITS);

		setDefaultState(blockState.getBaseState()
				.withProperty(NORTH, EnumAttachPosition.NONE)
				.withProperty(EAST, EnumAttachPosition.NONE)
				.withProperty(SOUTH, EnumAttachPosition.NONE)
				.withProperty(WEST, EnumAttachPosition.NONE));
		
		setHardness(0.0F);
		setSoundType(SoundType.STONE);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return WIRE_AABB[getAABBIndex(state.getActualState(source, pos))];
	}

	private static int getAABBIndex(IBlockState state) {
		int i = 0;
		boolean north = state.getValue(NORTH) != EnumAttachPosition.NONE;
		boolean east = state.getValue(EAST) != EnumAttachPosition.NONE;
		boolean south = state.getValue(SOUTH) != EnumAttachPosition.NONE;
		boolean west = state.getValue(WEST) != EnumAttachPosition.NONE;

		if(north || south && !north && !east && !west)
			i |= 1 << EnumFacing.NORTH.getHorizontalIndex();

		if(east || west && !north && !east && !south)
			i |= 1 << EnumFacing.EAST.getHorizontalIndex();

		if(south || north && !east && !south && !west)
			i |= 1 << EnumFacing.SOUTH.getHorizontalIndex();

		if(west || east && !north && !south && !west)
			i |= 1 << EnumFacing.WEST.getHorizontalIndex();

		return i;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		state = state.withProperty(WEST, getAttachPosition(worldIn, pos, EnumFacing.WEST));
		state = state.withProperty(EAST, getAttachPosition(worldIn, pos, EnumFacing.EAST));
		state = state.withProperty(NORTH, getAttachPosition(worldIn, pos, EnumFacing.NORTH));
		state = state.withProperty(SOUTH, getAttachPosition(worldIn, pos, EnumFacing.SOUTH));
		return state;
	}

	protected EnumAttachPosition getAttachPosition(IBlockAccess worldIn, BlockPos pos, EnumFacing direction) {
		BlockPos blockpos = pos.offset(direction);
		IBlockState iblockstate = worldIn.getBlockState(pos.offset(direction));

		if(!canConnectTo(worldIn.getBlockState(blockpos), direction, worldIn, blockpos) && (iblockstate.isNormalCube() || !canConnectUpwardsTo(worldIn, blockpos.down()))) {
			IBlockState iblockstate1 = worldIn.getBlockState(pos.up());

			if(!iblockstate1.isNormalCube()) {
				boolean flag = worldIn.getBlockState(blockpos).isSideSolid(worldIn, blockpos, EnumFacing.UP) || worldIn.getBlockState(blockpos).getBlock() == Blocks.GLOWSTONE;

				if(flag && canConnectUpwardsTo(worldIn, blockpos.up())) {
					if(iblockstate.isBlockNormalCube())
						return EnumAttachPosition.UP;

					return EnumAttachPosition.SIDE;
				}
			}

			return EnumAttachPosition.NONE;
		}
		else
		{
			return EnumAttachPosition.SIDE;
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return null;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos.down()).isTopSolid() || worldIn.getBlockState(pos.down()).getBlock() == Blocks.GLOWSTONE;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if(!worldIn.isRemote && !canPlaceBlockAt(worldIn, pos)) {
			dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}

	protected boolean canConnectUpwardsTo(IBlockAccess worldIn, BlockPos pos) {
		return canConnectTo(worldIn.getBlockState(pos), null, worldIn, pos);
	}

	protected boolean canConnectTo(IBlockState blockState, @Nullable EnumFacing side, IBlockAccess world, BlockPos pos) {
		Block block = blockState.getBlock();
		return block == this;
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(getItemDropped(state, worldIn.rand, 0));
	}

	@Override
	public IBlockState getStateFromMeta(int meta)  {
		return getDefaultState();
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		switch(rot) {
		case CLOCKWISE_180:
			return state.withProperty(NORTH, state.getValue(SOUTH)).withProperty(EAST, state.getValue(WEST)).withProperty(SOUTH, state.getValue(NORTH)).withProperty(WEST, state.getValue(EAST));
		case COUNTERCLOCKWISE_90:
			return state.withProperty(NORTH, state.getValue(EAST)).withProperty(EAST, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(WEST)).withProperty(WEST, state.getValue(NORTH));
		case CLOCKWISE_90:
			return state.withProperty(NORTH, state.getValue(WEST)).withProperty(EAST, state.getValue(NORTH)).withProperty(SOUTH, state.getValue(EAST)).withProperty(WEST, state.getValue(SOUTH));
		default:
			return state;
		}
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		switch(mirrorIn) {
		case LEFT_RIGHT:
			return state.withProperty(NORTH, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(NORTH));
		case FRONT_BACK:
			return state.withProperty(EAST, state.getValue(WEST)).withProperty(WEST, state.getValue(EAST));
		default:
			return super.withMirror(state, mirrorIn);
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { NORTH, EAST, SOUTH, WEST });
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(this);
	}

	public abstract int getColor(IBlockAccess world, IBlockState state, BlockPos pos, int tint);

	@Override
	public IBlockColor getBlockColor() {
		return (state, world, pos, tint) -> getColor(world, state, pos, tint);
	}

	@Override
	public IItemColor getItemColor() {
		return (stack, tint) -> 0xFFFFFF;
	}

	protected static enum EnumAttachPosition implements EnumBase {

		UP,
		SIDE,
		NONE

	}

}
