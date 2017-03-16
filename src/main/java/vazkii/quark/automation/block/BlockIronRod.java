package vazkii.quark.automation.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockIronRod extends BlockMod implements IQuarkBlock {

	protected static final AxisAlignedBB IRON_ROD_VERTICAL_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D);
	protected static final AxisAlignedBB IRON_ROD_NS_AABB = new AxisAlignedBB(0.375D, 0.375D, 0.0D, 0.625D, 0.625D, 1.0D);
	protected static final AxisAlignedBB IRON_ROD_EW_AABB = new AxisAlignedBB(0.0D, 0.375D, 0.375D, 1.0D, 0.625D, 0.625D);

	public static final PropertyDirection FACING = BlockDirectional.FACING;
	public static final PropertyBool CONNECTED = PropertyBool.create("connected");
	
	public BlockIronRod() {
		super("iron_rod", Material.IRON);
		setHardness(5.0F);
		setResistance(10.0F);
		setSoundType(SoundType.METAL);
		setCreativeTab(CreativeTabs.REDSTONE);
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
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch(((EnumFacing) state.getValue(FACING)).getAxis()) {
		case X:
			return IRON_ROD_EW_AABB;
		case Z:
			return IRON_ROD_NS_AABB;
		default:
			return IRON_ROD_VERTICAL_AABB;
		}
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		IBlockState iblockstate = worldIn.getBlockState(pos.offset(facing.getOpposite()));

		if(iblockstate.getBlock() == Blocks.END_ROD) {
			EnumFacing enumfacing = (EnumFacing)iblockstate.getValue(FACING);

			if(enumfacing == facing)
				return getDefaultState().withProperty(FACING, facing.getOpposite());
		}

		return getDefaultState().withProperty(FACING, facing);
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withProperty(FACING, mirrorIn.mirror((EnumFacing) state.getValue(FACING)));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumFacing) state.getValue(FACING)).getIndex();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState iblockstate = getDefaultState();
		iblockstate = iblockstate.withProperty(FACING, EnumFacing.getFront(meta));
		return iblockstate;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {FACING, CONNECTED});
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		IBlockState otherState = worldIn.getBlockState(pos.offset(state.getValue(FACING).getOpposite()));
		Block block = otherState.getBlock();
		return state.withProperty(CONNECTED, block != this);
	}
	
}
