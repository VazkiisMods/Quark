package vazkii.quark.automation.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import vazkii.quark.automation.entity.GravisandEntity;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.Module;

public class GravisandBlock extends QuarkBlock {

	public GravisandBlock(String regname, Module module, ItemGroup creativeTab, Properties properties) {
		super(regname, module, creativeTab, properties);
	}

//	public GravisandBlock() {
//		super("gravisand", Material.SAND);
//		setHardness(0.5F);
//		setSoundType(SoundType.SAND);
//		setCreativeTab(ItemGroup.REDSTONE);
//	}


	@Override
	public int tickRate(IWorldReader world) {
		return 2;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
		checkRedstone(world, pos);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		checkRedstone(worldIn, pos);
	}

	private void checkRedstone(World worldIn, BlockPos pos) {
        boolean powered = worldIn.isBlockPowered(pos);

        if(powered)
        	worldIn.getPendingBlockTicks().scheduleTick(pos, this, tickRate(worldIn));
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		return 15;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		if(!worldIn.isRemote) {
			if(checkFallable(worldIn, pos))
				for(Direction face : Direction.values()) {
					BlockPos offPos = pos.offset(face);
					BlockState offState = worldIn.getBlockState(offPos);
					
					if(offState.getBlock() == this)
			        	worldIn.getPendingBlockTicks().scheduleTick(offPos, this, tickRate(worldIn));
				}
		}
	}

	private boolean checkFallable(World worldIn, BlockPos pos) {
		if(!worldIn.isRemote) {
			if(tryFall(worldIn, pos, Direction.DOWN))
				return true;
			else return tryFall(worldIn, pos, Direction.UP);
		}
		
		return false;
	}
	
	private boolean tryFall(World worldIn, BlockPos pos, Direction facing) {
		BlockPos target = pos.offset(facing);
		if((worldIn.isAirBlock(target) || canFallThrough(worldIn, pos, worldIn.getBlockState(target))) && pos.getY() >= 0) {
			GravisandEntity entity = new GravisandEntity(worldIn, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, facing.getYOffset());
			worldIn.addEntity(entity);
			return true;
		}
		
		return false;
	}
	
    public static boolean canFallThrough(IWorldReader world, BlockPos pos, BlockState state) {
		Block block = state.getBlock();
		Material material = state.getMaterial();
		return state.isAir(world, pos) || block == Blocks.FIRE || material.isLiquid() || material.isReplaceable();
    }

}
