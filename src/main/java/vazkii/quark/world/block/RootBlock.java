package vazkii.quark.world.block;

import java.util.Random;
import java.util.function.BooleanSupplier;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.VineBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.module.Module;

public class RootBlock extends VineBlock implements IQuarkBlock, IGrowable {

	private final Module module;
	private BooleanSupplier enabledSupplier = () -> true;

	public RootBlock(Module module) {
		super(Block.Properties.create(Material.TALL_PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0.2F).sound(SoundType.PLANT));
		this.module = module;

		RegistryHelper.registerBlock(this, "root");
		RegistryHelper.setCreativeTab(this, ItemGroup.DECORATIONS);
	}
	
	@Override
	public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
		return true;
	}
	
	@Override
	public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity) {
		return false;
	}
	
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		if(!worldIn.isRemote && worldIn.rand.nextInt(2) == 0)
			grow(worldIn, random, pos, state);
	}
	
	public static void growMany(IWorld world, Random rand, BlockPos pos, BlockState state, float stopChance) {
		BlockPos next = pos;
		
		do {
			next = growAndReturnLastPos(world, next, state);
		} while(next != null && rand.nextFloat() >= stopChance);
	}

	public static BlockPos growAndReturnLastPos(IWorld world, BlockPos pos, BlockState state) {
		BlockPos down = pos.down();
		
		for(Direction facing : MiscUtil.HORIZONTALS) {
			BooleanProperty prop = getPropertyFor(facing);
			if(state.get(prop)) {
				BlockPos ret = growInFacing(world, down, facing);
				if(ret != null) {
					BlockState setState = state.getBlock().getDefaultState().with(prop, true);
					world.setBlockState(ret, setState, 2);
					return ret;
				}
				
				break;
			}
		}
		
		return null;
	}
	
	public static BlockPos growInFacing(IWorld world, BlockPos pos, Direction facing) {
		if(!world.isAirBlock(pos))
			return null;
		
		BlockPos check = pos.offset(facing);
		if(isAcceptableNeighbor(world, check, facing.getOpposite()))
			return pos;
		
		pos = check;
		if(!world.isAirBlock(check))
			return null;
		
		check = pos.offset(facing);
		if(isAcceptableNeighbor(world, check, facing.getOpposite()))
			return pos;
		
		return null;
	}

	public static boolean isAcceptableNeighbor(IWorld world, BlockPos pos, Direction side) {
		BlockState iblockstate = world.getBlockState(pos);
		return Block.hasSolidSide(iblockstate, world, pos, side) && iblockstate.getMaterial() == Material.ROCK;
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if(isEnabled() || group == ItemGroup.SEARCH)
			super.fillItemGroup(group, items);
	}

	@Nullable
	@Override
	public Module getModule() {
		return module;
	}

	@Override
	public RootBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

	@Override
	public boolean canGrow(IBlockReader world, BlockPos pos, BlockState state, boolean client) {
		return world.getLightValue(pos) < 7;
	}

	@Override
	public boolean canUseBonemeal(World world, Random rand, BlockPos pos, BlockState state) {
		return rand.nextFloat() < 0.4;
	}
	
	@Override
	public void grow(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
		growAndReturnLastPos(world, pos, state);
	}
	
}
