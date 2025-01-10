package org.violetmoon.quark.mixin.mixins.self;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.extensions.IBlockExtension;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.violetmoon.quark.addons.oddities.block.MatrixEnchantingTableBlock;
import org.violetmoon.quark.content.automation.block.IronRodBlock;
import org.violetmoon.quark.content.building.block.*;
import org.violetmoon.quark.content.world.block.HugeGlowShroomBlock;
import org.violetmoon.zeta.block.ext.IZetaBlockExtensions;

// Forge can't actually mixin to interfaces, so we fake it by just... mixing in to everyone inheriting the interface.
// Copy of the same class from Zeta for quark blocks
@Mixin({
	HedgeBlock.class,
	HugeGlowShroomBlock.class,
	IronRodBlock.class,
	MatrixEnchantingTableBlock.class,
	QuarkVerticalSlabBlock.class,
	VariantChestBlock.class,
	VariantFurnaceBlock.class,
	VariantLadderBlock.class,
	VariantTrappedChestBlock.class,
	VerticalSlabBlock.class
})
public class IZetaBlockMixin_FAKE implements IZetaBlockExtensions, IBlockExtension {

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
	public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos soilPosition, Direction facing, BlockState plant) {
		return TriState.DEFAULT;
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
	public @Nullable Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
		return state.getBlock() instanceof BeaconBeamBlock bbeam ? bbeam.getColor().getTextureDiffuseColor() : null;
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
	public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility toolAction, boolean simulate) {
		String toolActionName = toolAction.name();
		return getToolModifiedStateZeta(state, context, ItemAbility.get(toolActionName), simulate);
	}

	@Override
	public boolean isScaffolding(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
		return isScaffoldingZeta(state, level, pos, entity);
	}

}
