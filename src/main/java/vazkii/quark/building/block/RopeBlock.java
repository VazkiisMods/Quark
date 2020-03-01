package vazkii.quark.building.block;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import vazkii.arl.interf.IBlockItemProvider;
import vazkii.quark.automation.module.PistonsMoveTileEntitiesModule;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.Module;
import vazkii.quark.building.module.RopeModule;

public class RopeBlock extends QuarkBlock implements IBlockItemProvider {

	private static final VoxelShape SHAPE = makeCuboidShape(6, 0, 6, 10, 16, 10);

	public RopeBlock(String regname, Module module, ItemGroup creativeTab, Properties properties) {
		super(regname, module, creativeTab, properties);
		
		RenderLayerHandler.setRenderType(this, RenderTypeSkeleton.CUTOUT);
	}
	
	@Override
	public BlockItem provideItemBlock(Block block, Item.Properties properties) {
		return new BlockItem(block, properties) {
			@Override
			public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
				return world.getBlockState(pos).getBlock() instanceof RopeBlock;
			}
		};
	}

	@Override
	@SuppressWarnings("deprecation")
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if(hand == Hand.MAIN_HAND) {
			ItemStack stack = player.getHeldItem(hand);
			if(stack.getItem() == asItem() && !player.isDiscrete()) {
				if(pullDown(worldIn, pos)) {
					if(!player.isCreative())
						stack.shrink(1);
					
					worldIn.playSound(null, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, 0.5F, 1F);
					return ActionResultType.SUCCESS;
				}
			} else if (stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
				return FluidUtil.interactWithFluidHandler(player, hand, worldIn, getBottomPos(worldIn, pos), Direction.UP) ? ActionResultType.SUCCESS : ActionResultType.PASS;
			} else if (stack.getItem() == Items.GLASS_BOTTLE) {
				BlockPos bottomPos = getBottomPos(worldIn, pos);
				BlockState stateAt = worldIn.getBlockState(bottomPos);
				if (stateAt.getMaterial() == Material.WATER) {
					Vec3d playerPos = player.getPositionVec();
					worldIn.playSound(player, playerPos.x, playerPos.y, playerPos.z, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
					stack.shrink(1);
					ItemStack bottleStack = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER);
					player.addStat(Stats.ITEM_USED.get(stack.getItem()));

					if (stack.isEmpty())
						player.setHeldItem(hand, bottleStack);
					else if (!player.inventory.addItemStackToInventory(bottleStack))
						player.dropItem(bottleStack, false);


					return ActionResultType.SUCCESS;
				}

				return ActionResultType.PASS;
			} else {
				if(pullUp(worldIn, pos)) {
					if(!player.isCreative()) {
						if(!player.addItemStackToInventory(new ItemStack(this)))
							player.dropItem(new ItemStack(this), false);
					}
					
					worldIn.playSound(null, pos, soundType.getBreakSound(), SoundCategory.BLOCKS, 0.5F, 1F);
					return ActionResultType.SUCCESS;
				}
			}
		}
		
		return ActionResultType.PASS;
	}

	public boolean pullUp(World world, BlockPos pos) {
		BlockPos basePos = pos;
		
		while(true) {
			pos = pos.down();
			BlockState state = world.getBlockState(pos);
			if(state.getBlock() != this)
				break;
		}
		
		BlockPos ropePos = pos.up();
		if(ropePos.equals(basePos))
			return false;

		world.setBlockState(ropePos, Blocks.AIR.getDefaultState());
		moveBlock(world, pos, ropePos);
		
		return true;
	}
	
	public boolean pullDown(World world, BlockPos pos) {
		boolean can;
		boolean endRope = false;
		boolean wasAirAtEnd = false;
		
		do {
			pos = pos.down();
			if (!World.isValid(pos))
				return false;

			BlockState state = world.getBlockState(pos);
			Block block = state.getBlock();
			
			if(block == this)
				continue;
			
			if(endRope) {
				can = wasAirAtEnd || world.isAirBlock(pos) || state.getMaterial().isReplaceable();
				break;
			}
			
			endRope = true;
			wasAirAtEnd = world.isAirBlock(pos);
		} while(true);
		
		if(can) {
			BlockPos ropePos = pos.up();
			moveBlock(world, ropePos, pos);
			
			BlockState ropePosState = world.getBlockState(ropePos);

			if(world.isAirBlock(ropePos) || ropePosState.getMaterial().isReplaceable()) {
				world.setBlockState(ropePos, getDefaultState());
				return true;
			}
		}
		
		return false;
	}

	private BlockPos getBottomPos(World worldIn, BlockPos pos) {
		Block block = this;
		while (block == this) {
			pos = pos.down();
			BlockState state = worldIn.getBlockState(pos);
			block = state.getBlock();
		}

		return pos;

	}

	private void moveBlock(World world, BlockPos srcPos, BlockPos dstPos) {
		BlockState state = world.getBlockState(srcPos);
		Block block = state.getBlock();
		
		if(state.getBlockHardness(world, srcPos) == -1 || !state.isValidPosition(world, dstPos) || block.isAir(state, world, srcPos) ||
				state.getPushReaction() != PushReaction.NORMAL || block == Blocks.OBSIDIAN)
			return;
		
		TileEntity tile = world.getTileEntity(srcPos);
		if(tile != null) {
			if(RopeModule.forceEnableMoveTileEntities ? PistonsMoveTileEntitiesModule.shouldMoveTE(state) : PistonsMoveTileEntitiesModule.shouldMoveTE(true, state))
				return;

			tile.remove();
		}
		
		world.setBlockState(srcPos, Blocks.AIR.getDefaultState());
		world.setBlockState(dstPos, state);
		
		if(tile != null) {
			tile.setPos(dstPos);
			TileEntity target = TileEntity.create(tile.write(new CompoundNBT()));
			if (target != null) {
				world.setTileEntity(dstPos, target);

				target.updateContainingBlockInfo();

			}
		}

		world.notifyNeighbors(dstPos, state.getBlock());
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		BlockPos upPos = pos.up();
		BlockState upState = worldIn.getBlockState(upPos);
		return upState.getBlock() == this || upState.isSolidSide(worldIn, upPos, Direction.DOWN);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if(!state.isValidPosition(worldIn, pos)) {
			worldIn.playEvent(2001, pos, Block.getStateId(worldIn.getBlockState(pos)));
			spawnDrops(state, worldIn, pos);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
		}
	}

	@Override
	public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity) {
		return true;
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@Override
	public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return 30;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return 60;
	}

}
