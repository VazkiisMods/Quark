package org.violetmoon.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.quark.base.block.QuarkBlock;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.IZetaBlockColorProvider;
import org.violetmoon.zeta.registry.RenderLayerRegistry;

import javax.annotation.Nonnull;

public class LeafCarpetBlock extends QuarkBlock implements IZetaBlockColorProvider {

	private static final VoxelShape SHAPE = box(0, 0, 0, 16, 1, 16);

	public final BlockState baseState;
	private ItemStack baseStack;

	public LeafCarpetBlock(String name, Block base, ZetaModule module) {
		super(name, module, CreativeModeTab.TAB_DECORATIONS,
				Block.Properties.of(Material.CLOTH_DECORATION, base.defaultBlockState().materialColor)
						.strength(0F)
						.sound(SoundType.GRASS)
						.noOcclusion());

		baseState = base.defaultBlockState();

		module.zeta.renderLayerRegistry.put(this, RenderLayerRegistry.Layer.CUTOUT_MIPPED);
	}

	public BlockState getBaseState() {
		return baseState;
	}

	@Override
	public boolean canBeReplaced(@Nonnull BlockState state, @Nonnull BlockPlaceContext useContext) {
		return useContext.getItemInHand().isEmpty() || useContext.getItemInHand().getItem() != this.asItem();
	}

	@Nonnull
	@Override
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
		return SHAPE;
	}

	@Nonnull
	@Override
	public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
		return Shapes.empty();
	}

	@Nonnull
	@Override

	public BlockState updateShape(BlockState state, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull LevelAccessor world, @Nonnull BlockPos pos, @Nonnull BlockPos facingPos) {
		return !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, pos, facingPos);
	}

	@Override
	public boolean canSurvive(@Nonnull BlockState state, LevelReader world, BlockPos pos) {
		return !world.isEmptyBlock(pos.below());
	}

	@Override
	public @Nullable String getBlockColorProviderName() {
		return "leaf_carpet";
	}

	@Override
	public @Nullable String getItemColorProviderName() {
		return "leaf_carpet";
	}

}
