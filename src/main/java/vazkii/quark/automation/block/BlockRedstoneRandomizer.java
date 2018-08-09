package vazkii.quark.automation.block;

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMod;
import vazkii.arl.util.RotationHandler;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockRedstoneRandomizer extends BlockMod implements IQuarkBlock {

	protected static final AxisAlignedBB REDSTONE_DIODE_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);

	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final PropertyBool POWERED = PropertyBool.create("powered");
	public static final PropertyBool POWER_LEFT = PropertyBool.create("power_left");

	public BlockRedstoneRandomizer() {
		super("redstone_randomizer", Material.CIRCUITS);
		
		setDefaultState(blockState.getBaseState()
					.withProperty(FACING, EnumFacing.NORTH)
					.withProperty(POWERED, false)
					.withProperty(POWER_LEFT, false));
		setCreativeTab(CreativeTabs.REDSTONE);
		setSoundType(SoundType.WOOD);
	}

	protected int getActiveSignal(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
		return (isPowered(state) && side == getOutputFace(state)) ? 15 : 0; 
	}

	protected void updateState(World world, BlockPos pos, IBlockState currState) {
		boolean isPowered = isPowered(currState);
		boolean willBePowered = shouldBePowered(world, pos, currState);
		if(isPowered != willBePowered) {
			IBlockState target = currState.withProperty(POWERED, willBePowered);
			
			if(willBePowered)
				target = target.withProperty(POWER_LEFT, world.rand.nextBoolean());
			
			world.setBlockState(pos, target);
		}
	}
	
	private EnumFacing getInputFace(IBlockState state) {
		return correct(state, EnumFacing.SOUTH);
	}
	
	private EnumFacing getOutputFace(IBlockState state) {
		EnumFacing target = state.getValue(POWER_LEFT) ? EnumFacing.WEST : EnumFacing.EAST;
		return correct(state, target);
	}
	
	private EnumFacing correct(IBlockState state, EnumFacing target) {
		return RotationHandler.rotateFacing(target, state.getValue(FACING));
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING, POWERED, POWER_LEFT });
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return (state.getValue(FACING).ordinal() - 2) + (state.getValue(POWERED) ? 0b0100 : 0) + (state.getValue(POWER_LEFT) ? 0b1000 : 0);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing face = EnumFacing.VALUES[(meta & 0b0011) + 2];
		boolean powered = (meta & 0b0100) != 0;
		boolean left = (meta & 0b1000) != 0;
		return getDefaultState().withProperty(FACING, face).withProperty(POWERED, powered).withProperty(POWER_LEFT, left);
	}

	// ===========================================================================
	// ALL VANILLA COPY PASTA FROM HERE ON OUT
	// ===========================================================================

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return REDSTONE_DIODE_AABB;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos.down()).isTopSolid() ? super.canPlaceBlockAt(worldIn, pos) : false;
	}

	public boolean canBlockStay(World worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos.down()).isTopSolid();
	}

	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return side.getAxis() != EnumFacing.Axis.Y;
	}

	protected boolean isPowered(IBlockState state) {
		return state.getValue(POWERED);
	}

	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return blockState.getWeakPower(blockAccess, pos, side);
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		if(!isPowered(blockState))
			return 0;
		else
			return getActiveSignal(blockAccess, pos, blockState, side);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if(canBlockStay(worldIn, pos))
			updateState(worldIn, pos, state);
		else {
			dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);

			for(EnumFacing enumfacing : EnumFacing.values())
				worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false);
		}
	}
	
    protected int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state) {
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
        BlockPos blockpos = pos.offset(enumfacing);
        int i = worldIn.getRedstonePower(blockpos, enumfacing);

        if(i >= 15)
            return i;
        else {
            IBlockState iblockstate = worldIn.getBlockState(blockpos);
            return Math.max(i, iblockstate.getBlock() == Blocks.REDSTONE_WIRE ? ((Integer)iblockstate.getValue(BlockRedstoneWire.POWER)).intValue() : 0);
        }
    }
    
	protected boolean shouldBePowered(World world, BlockPos pos, IBlockState currState) {
		return calculateInputStrength(world, pos, currState) > 0;
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if(shouldBePowered(worldIn, pos, state))
			worldIn.scheduleUpdate(pos, this, 1);
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		notifyNeighbors(worldIn, pos, state);
	}

	protected void notifyNeighbors(World worldIn, BlockPos pos, IBlockState state) {
		EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
		BlockPos blockpos = pos.offset(enumfacing.getOpposite());
		if(ForgeEventFactory.onNeighborNotify(worldIn, pos, worldIn.getBlockState(pos), EnumSet.of(enumfacing.getOpposite()), false).isCanceled())
			return;

		worldIn.neighborChanged(blockpos, this, pos);
		worldIn.notifyNeighborsOfStateExcept(blockpos, this, enumfacing);
	}

	@Override
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
		if(isPowered(state))
			for(EnumFacing enumfacing : EnumFacing.values())
				worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false);

		super.onBlockDestroyedByPlayer(worldIn, pos, state);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		if(super.rotateBlock(world, pos, axis)) {
			IBlockState state = world.getBlockState(pos);
			state = state.withProperty(POWERED, false);
			world.setBlockState(pos, state);

			if(shouldBePowered(world, pos, state))
				world.scheduleUpdate(pos, this, 1);
			return true;
		}
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

}
