package vazkii.zetaimplforge.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.ToolAction;
import org.jetbrains.annotations.Nullable;
import vazkii.zeta.block.ext.IZetaBlockExtensions;

//dumb class name; (IForgeBlock)(BlockExtensions); implementation of IZetaBlockExtensions using methods from IForgeBlock
public class IForgeBlockBlockExtensions implements IZetaBlockExtensions {

	public static final IForgeBlockBlockExtensions INSTANCE = new IForgeBlockBlockExtensions();

	@Override
	public int getLightEmissionZeta(BlockState state, BlockGetter level, BlockPos pos) {
		return state.getLightEmission(level, pos);
	}

	@Override
	public boolean isLadderZeta(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
		return state.isLadder(level, pos, entity);
	}

	@Override
	public boolean makesOpenTrapdoorAboveClimbableZeta(BlockState state, LevelReader level, BlockPos pos, BlockState trapdoorState) {
		//not exposed through BlockState
		return state.getBlock().makesOpenTrapdoorAboveClimbable(state, level, pos, trapdoorState);
	}

	@Override
	public boolean canSustainPlantZeta(BlockState state, BlockGetter level, BlockPos pos, Direction facing, String plantabletype) {
		return false; //TODO thread the IPlantable through
	}

	@Override
	public boolean isConduitFrameZeta(BlockState state, LevelReader level, BlockPos pos, BlockPos conduit) {
		return state.isConduitFrame(level, pos, conduit);
	}

	@Override
	public float getEnchantPowerBonusZeta(BlockState state, LevelReader level, BlockPos pos) {
		return state.getEnchantPowerBonus(level, pos);
	}

	@Override
	public SoundType getSoundTypeZeta(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
		return state.getSoundType(level, pos, entity);
	}

	@Override
	public float[] getBeaconColorMultiplierZeta(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
		return state.getBeaconColorMultiplier(level, pos, beaconPos);
	}

	@Override
	public boolean isStickyBlockZeta(BlockState state) {
		return state.isStickyBlock();
	}

	@Override
	public boolean canStickToZeta(BlockState state, BlockState other) {
		return state.canStickTo(other);
	}

	@Override
	public int getFlammabilityZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return state.getFlammability(world, pos, face);
	}

	@Override
	public boolean isFlammableZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return state.isFlammable(world, pos, face);
	}

	@Override
	public int getFireSpreadSpeedZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return state.getFireSpreadSpeed(world, pos, face);
	}

	@Override
	public boolean collisionExtendsVerticallyZeta(BlockState state, BlockGetter level, BlockPos pos, Entity collidingEntity) {
		return state.collisionExtendsVertically(level, pos, collidingEntity);
	}

	@Override
	public boolean shouldDisplayFluidOverlayZeta(BlockState state, BlockAndTintGetter level, BlockPos pos, FluidState fluidState) {
		return state.shouldDisplayFluidOverlay(level, pos, fluidState);
	}

	@Override
	public @Nullable BlockState getToolModifiedStateZeta(BlockState state, UseOnContext context, String toolActionType, boolean simulate) {
		ToolAction action = ToolAction.get(toolActionType);
		if(action == null)
			return null;
		else
			return state.getToolModifiedState(context, action, simulate);
	}

	@Override
	public boolean isScaffoldingZeta(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
		//forge doesn't delegate this one to the blockstate, hmm
		return state.getBlock().isScaffolding(state, level, pos, entity);
	}

}
