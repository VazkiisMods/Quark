package org.violetmoon.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import org.jetbrains.annotations.Nullable;

import org.violetmoon.zeta.block.ZetaBlock;
import org.violetmoon.zeta.module.ZetaModule;

public class BambooMatBlock extends ZetaBlock {

	private static final EnumProperty<Direction> FACING = BlockStateProperties.FACING_HOPPER;

	public BambooMatBlock(String name, @Nullable ZetaModule module) {
		this(name, module, CreativeModeTabs.BUILDING_BLOCKS);
	}

	public BambooMatBlock(String name, @Nullable ZetaModule module, ResourceKey<CreativeModeTab> tab) {
		super(name, module,
				Block.Properties.of()
						.mapColor(MapColor.COLOR_YELLOW)
						.forceSolidOn()
						.strength(0.5F)
						.sound(SoundType.BAMBOO)
						.ignitedByLava()
						.pushReaction(PushReaction.DESTROY)
						.isRedstoneConductor((s, r, p) -> false)
		);

		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));

		if(module == null) //auto registration below this line
			return;

		setCreativeTab(tab);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Direction dir = ctx.getHorizontalDirection();
		if(ctx.getPlayer().getXRot() > 70)
			dir = Direction.DOWN;

		if(dir != Direction.DOWN) {
			Direction opposite = dir.getOpposite();
			BlockPos target = ctx.getClickedPos().relative(opposite);
			BlockState state = ctx.getLevel().getBlockState(target);

			if(state.getBlock() != this || state.getValue(FACING) != opposite) {
				target = ctx.getClickedPos().relative(dir);
				state = ctx.getLevel().getBlockState(target);

				if(state.getBlock() == this && state.getValue(FACING) == dir)
					dir = opposite;
			}
		}

		return defaultBlockState().setValue(FACING, dir);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
}
