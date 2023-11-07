package vazkii.zeta.block.ext;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

/**
 * Imagine a mouse eating a suspiciously IForgeBlock-shaped piece of cheese
 */
@SuppressWarnings("deprecation") //Forge deprecating shit in favor of their replacements
public interface IZetaBlockExtensions {

	IZetaBlockExtensions DEFAULT = new IZetaBlockExtensions() { };

	default int getLightEmissionZeta(BlockState state, BlockGetter level, BlockPos pos) {
		return state.getLightEmission();
	}

	default boolean isLadderZeta(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
		return state.is(BlockTags.CLIMBABLE);
	}

	default boolean makesOpenTrapdoorAboveClimbableZeta(BlockState state, LevelReader level, BlockPos pos, BlockState trapdoorState) {
		return state.getBlock() instanceof LadderBlock && state.getValue(LadderBlock.FACING) == trapdoorState.getValue(TrapDoorBlock.FACING);
	}

	default boolean canSustainPlantZeta(BlockState state, BlockGetter level, BlockPos pos, Direction facing, String plantabletype) {
		return false;
	}

	default boolean isConduitFrameZeta(BlockState state, LevelReader level, BlockPos pos, BlockPos conduit) {
		return state.getBlock() == Blocks.PRISMARINE ||
			state.getBlock() == Blocks.PRISMARINE_BRICKS ||
			state.getBlock() == Blocks.SEA_LANTERN ||
			state.getBlock() == Blocks.DARK_PRISMARINE;
	}

	default float getEnchantPowerBonusZeta(BlockState state, LevelReader level, BlockPos pos) {
		return state.is(Blocks.BOOKSHELF) ? 1 : 0;
	}

	default SoundType getSoundTypeZeta(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
		return state.getSoundType();
	}

	default float[] getBeaconColorMultiplierZeta(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
		if(state.getBlock() instanceof BeaconBeamBlock bbeam)
			return bbeam.getColor().getTextureDiffuseColors();
		return null;
	}

	default boolean isStickyBlockZeta(BlockState state) {
		return state.getBlock() == Blocks.SLIME_BLOCK || state.getBlock() == Blocks.HONEY_BLOCK;
	}

	default boolean canStickToZeta(BlockState state, BlockState other) {
		if(state.getBlock() == Blocks.HONEY_BLOCK && other.getBlock() == Blocks.SLIME_BLOCK)
			return false;
		else if(state.getBlock() == Blocks.SLIME_BLOCK && other.getBlock() == Blocks.HONEY_BLOCK)
			return false;
		else return state.isStickyBlock() || other.isStickyBlock();
	}

	default int getFlammabilityZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return ((FireBlock) Blocks.FIRE).getBurnOdds(state);
	}

	default boolean isFlammableZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return getFlammabilityZeta(state, world, pos, face) > 0;
	}

	default int getFireSpreadSpeedZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return ((FireBlock)Blocks.FIRE).getIgniteOdds(state);
	}

	default boolean collisionExtendsVerticallyZeta(BlockState state, BlockGetter level, BlockPos pos, Entity collidingEntity) {
		return state.is(BlockTags.FENCES) || state.is(BlockTags.WALLS) || state.getBlock() instanceof FenceGateBlock;
	}

	default boolean shouldDisplayFluidOverlayZeta(BlockState state, BlockAndTintGetter level, BlockPos pos, FluidState fluidState) {
		return state.getBlock() instanceof HalfTransparentBlock || state.getBlock() instanceof LeavesBlock;
	}

	@Nullable
	default BlockState getToolModifiedStateZeta(BlockState state, UseOnContext context, String toolActionType, boolean simulate) {
		//TODO, check i copied forge correctly

		//ItemStack itemStack = context.getItemInHand();
		//if (!itemStack.canPerformAction(toolAction)) //Forge extension, TODO when i make an IZetaItemExtensions
		//	return null;

		return switch(toolActionType) {
			case "axe_strip" -> AxeItem.getAxeStrippingState(state); //TODO forge extension
			case "axe_scrape" -> WeatheringCopper.getPrevious(state).orElse(null);
			case "axe_wax_off" -> Optional.ofNullable(HoneycombItem.WAX_OFF_BY_BLOCK.get().get(state.getBlock())).map(block -> block.withPropertiesOf(state)).orElse(null);
			case "shovel_flatten" -> ShovelItem.getShovelPathingState(state); //TODO forge extension
			case "hoe_till" -> {
				Block block = state.getBlock();
				if (block == Blocks.ROOTED_DIRT) {
					if (!simulate && !context.getLevel().isClientSide) {
						Block.popResourceFromFace(context.getLevel(), context.getClickedPos(), context.getClickedFace(), new ItemStack(Items.HANGING_ROOTS));
					}
					yield Blocks.DIRT.defaultBlockState();
				} else if ((block == Blocks.GRASS_BLOCK || block == Blocks.DIRT_PATH || block == Blocks.DIRT || block == Blocks.COARSE_DIRT) && context.getLevel().getBlockState(context.getClickedPos().above()).isAir())
					yield block == Blocks.COARSE_DIRT ? Blocks.DIRT.defaultBlockState() : Blocks.FARMLAND.defaultBlockState();
				else
					yield null;
			}
			default -> null;
		};
	}

	default boolean isScaffoldingZeta(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
		return state.is(Blocks.SCAFFOLDING);
	}

}
