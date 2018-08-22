package vazkii.quark.oddities.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockWall;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import vazkii.arl.block.BlockMetaVariants.EnumBase;
import vazkii.arl.block.BlockModContainer;
import vazkii.quark.base.block.BlockQuarkWall;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.oddities.tile.TilePipe;

public class BlockPipe extends BlockModContainer implements IQuarkBlock {

	private static final AxisAlignedBB CENTER_AABB = new AxisAlignedBB(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875);

	private static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(0.3125, 0, 0.3125, 0.6875, 0.6875, 0.6875);
	private static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.3125, 0.3125, 0.3125, 0.6875, 1, 0.6875);
	private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.3125, 0.3125, 0, 0.6875, 0.6875, 0.6875);
	private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 1);
	private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875);
	private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.3125, 0.3125, 1, 0.6875, 0.6875, 0.6875);

	public static final PropertyEnum<ConnectionType> DOWN = PropertyEnum.create("down", ConnectionType.class);
	public static final PropertyEnum<ConnectionType> UP = PropertyEnum.create("up", ConnectionType.class);
	public static final PropertyEnum<ConnectionType> NORTH = PropertyEnum.create("north", ConnectionType.class);
	public static final PropertyEnum<ConnectionType> SOUTH = PropertyEnum.create("south", ConnectionType.class);
	public static final PropertyEnum<ConnectionType> WEST = PropertyEnum.create("west", ConnectionType.class);
	public static final PropertyEnum<ConnectionType> EAST = PropertyEnum.create("east", ConnectionType.class);
	public static final PropertyBool ENABLED = PropertyBool.create("enabled");

	private static final PropertyEnum<ConnectionType>[] CONNECTIONS = new PropertyEnum[] {
			DOWN, UP, NORTH, SOUTH, WEST, EAST
	};

	private static final AxisAlignedBB[] SIDE_BOXES = new AxisAlignedBB[] {
			DOWN_AABB, UP_AABB, NORTH_AABB, SOUTH_AABB, WEST_AABB, EAST_AABB
	};

	public BlockPipe() {
		super("pipe", Material.GLASS);
		setHardness(3.0F);
		setResistance(10.0F);
		setSoundType(SoundType.GLASS);
		setCreativeTab(CreativeTabs.REDSTONE);

		setHarvestLevel("pickaxe", 1);

		setDefaultState(getDefaultState()
				.withProperty(DOWN, ConnectionType.NONE).withProperty(UP, ConnectionType.NONE)
				.withProperty(NORTH, ConnectionType.NONE).withProperty(SOUTH, ConnectionType.NONE)
				.withProperty(WEST, ConnectionType.NONE).withProperty(EAST, ConnectionType.NONE)
				.withProperty(ENABLED, true));
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		boolean flag = !worldIn.isBlockPowered(pos);

		if(flag != state.getValue(ENABLED))
			worldIn.setBlockState(pos, state.withProperty(ENABLED, Boolean.valueOf(flag)), 2 | 4);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		double minX = CENTER_AABB.minX, minY = CENTER_AABB.minY, minZ = CENTER_AABB.minZ, 
				maxX = CENTER_AABB.maxX, maxY = CENTER_AABB.maxY, maxZ = CENTER_AABB.maxZ;

		state = getActualState(state, source, pos);
		if(hasAnyConnection(state, EnumFacing.DOWN)) minY = 0;
		if(hasAnyConnection(state, EnumFacing.UP)) maxY = 1;
		if(hasAnyConnection(state, EnumFacing.NORTH)) minZ = 0;
		if(hasAnyConnection(state, EnumFacing.SOUTH)) maxZ = 1;
		if(hasAnyConnection(state, EnumFacing.WEST)) minX = 0;
		if(hasAnyConnection(state, EnumFacing.EAST)) maxX = 1;

		AxisAlignedBB aabb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
		return aabb;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
		return getBoundingBox(state, worldIn, pos).offset(pos);
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
		if(!isActualState)
			state = getActualState(state, worldIn, pos);

		addCollisionBoxToList(pos, entityBox, collidingBoxes, CENTER_AABB);
		for(EnumFacing side : EnumFacing.VALUES) {
			if(hasAnyConnection(state, side))
				addCollisionBoxToList(pos, entityBox, collidingBoxes, SIDE_BOXES[side.ordinal()]);
		}
	}

	private boolean hasAnyConnection(IBlockState state, EnumFacing side) {
		PropertyEnum<ConnectionType> prop = CONNECTIONS[side.ordinal()];
		return state.getValue(prop).isSolid;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public IProperty[] getIgnoredProperties() {
		return new IProperty[] { ENABLED };
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { UP, DOWN, NORTH, SOUTH, WEST, EAST, ENABLED });
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return (state.getValue(ENABLED) ? 0b0 : 1);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(ENABLED, (meta & 0b1) != 1);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		IBlockState actualState = state;
		for(EnumFacing facing : EnumFacing.VALUES) {
			PropertyEnum<ConnectionType> prop = CONNECTIONS[facing.ordinal()];
			ConnectionType type = getConnectionTo(worldIn, pos, facing);
			actualState = actualState.withProperty(prop, type);
		}

		return actualState;
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile instanceof TilePipe)
			return ((TilePipe) tile).getComparatorOutput();
		return 0;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if(tileentity instanceof TilePipe)
			((TilePipe) tileentity).dropAllItems();;

			super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return true;
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TilePipe();
	}

	private ConnectionType getConnectionTo(IBlockAccess world, BlockPos pos, EnumFacing face) {
		pos = pos.offset(face);
		TileEntity tile = world.getTileEntity(pos);
		if(tile != null) {
			if(tile instanceof TilePipe)
				return ConnectionType.PIPE;
			else if(tile instanceof IInventory || (tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite()) 
					&& tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite()) != null))
				return ConnectionType.TERMINAL;
		}

		IBlockState stateAt = world.getBlockState(pos);
		Block blockAt = stateAt.getBlock();
		if((face.getAxis() == Axis.Y && (blockAt instanceof BlockWall || blockAt instanceof BlockQuarkWall))
				|| ((blockAt instanceof BlockPistonBase || blockAt instanceof BlockPistonExtension) && stateAt.getValue(BlockDirectional.FACING) == face.getOpposite()))
				return ConnectionType.PROP;

		return ConnectionType.NONE;
	}

	public static enum ConnectionType implements EnumBase {

		NONE(false, false), 
		PIPE(true, true), 
		TERMINAL(true, true), 
		PROP(true, false);

		private ConnectionType(boolean isSolid, boolean allowsItems) {
			this.isSolid = isSolid;
			this.allowsItems = allowsItems;
		}

		public final boolean isSolid, allowsItems;

	}

}
