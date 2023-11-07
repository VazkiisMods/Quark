package vazkii.quark.mixin.zeta_forge.self;

import java.util.Locale;

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
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import vazkii.quark.addons.oddities.block.MatrixEnchantingTableBlock;
import vazkii.quark.base.block.*;
import vazkii.quark.content.automation.block.IronRodBlock;
import vazkii.quark.content.building.block.*;
import vazkii.quark.content.world.block.HugeGlowShroomBlock;
import vazkii.zeta.block.ext.IZetaBlockExtensions;

// Kid named forge interface mixins:
@Mixin({
	HedgeBlock.class,
	HugeGlowShroomBlock.class,
	IronRodBlock.class,
	MatrixEnchantingTableBlock.class,
	QuarkBlock.class,
	QuarkBlockWrapper.class,
	QuarkBushBlock.class,
	QuarkButtonBlock.class,
	QuarkDoorBlock.class,
	QuarkFenceBlock.class,
	QuarkFenceGateBlock.class,
	QuarkInheritedPaneBlock.class,
	QuarkLeavesBlock.class,
	QuarkPaneBlock.class,
	QuarkPillarBlock.class,
	QuarkPressurePlateBlock.class,
	QuarkSaplingBlock.class,
	QuarkSlabBlock.class,
	QuarkStairsBlock.class,
	QuarkStandingSignBlock.class,
	QuarkTrapdoorBlock.class,
	QuarkVerticalSlabBlock.class,
	QuarkVineBlock.class,
	QuarkWallBlock.class,
	QuarkWallSignBlock.class,
	VariantChestBlock.class,
	VariantFurnaceBlock.class,
	VariantLadderBlock.class,
	VariantTrappedChestBlock.class,
	VerticalSlabBlock.class,
})
public class IQuarkBlockMixin implements IZetaBlockExtensions, IForgeBlock {

	@Override
	public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
		return getLightEmissionZeta(state, level, pos);
	}

	@Override
	public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
		return isLadderZeta(state, level, pos, entity);
	}

	@Override
	public boolean makesOpenTrapdoorAboveClimbable(BlockState state, LevelReader level, BlockPos pos, BlockState trapdoorState) {
		return makesOpenTrapdoorAboveClimbableZeta(state, level, pos, trapdoorState);
	}

	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction facing, IPlantable plantable) {
		return canSustainPlantZeta(state, level, pos, facing, plantable.getPlantType(level, pos).getName().toLowerCase(Locale.ROOT));
	}

	@Override
	public boolean isConduitFrame(BlockState state, LevelReader level, BlockPos pos, BlockPos conduit) {
		return isConduitFrameZeta(state, level, pos, conduit);
	}

	@Override
	public float getEnchantPowerBonus(BlockState state, LevelReader level, BlockPos pos) {
		return getEnchantPowerBonusZeta(state, level, pos);
	}

	@Override
	public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
		return getSoundTypeZeta(state, level, pos, entity);
	}

	@Override
	public @Nullable float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
		return getBeaconColorMultiplierZeta(state, level, pos, beaconPos);
	}

	@Override
	public boolean isStickyBlock(BlockState state) {
		return isStickyBlockZeta(state);
	}

	@Override
	public boolean canStickTo(BlockState state, BlockState other) {
		return canStickToZeta(state, other);
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return getFlammabilityZeta(state, level, pos, direction);
	}

	@Override
	public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return isFlammableZeta(state, level, pos, direction);
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return getFireSpreadSpeedZeta(state, level, pos, direction);
	}

	@Override
	public boolean collisionExtendsVertically(BlockState state, BlockGetter level, BlockPos pos, Entity collidingEntity) {
		return collisionExtendsVerticallyZeta(state, level, pos, collidingEntity);
	}

	@Override
	public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter level, BlockPos pos, FluidState fluidState) {
		return shouldDisplayFluidOverlayZeta(state, level, pos, fluidState);
	}

	@Override
	public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
		String toolActionName = toolAction.name();
		return getToolModifiedStateZeta(state, context, toolActionName, simulate);
	}

	@Override
	public boolean isScaffolding(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
		return isScaffoldingZeta(state, level, pos, entity);
	}

}
