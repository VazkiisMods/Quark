package org.violetmoon.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.violetmoon.zeta.block.OldMaterials;
import org.violetmoon.zeta.block.ZetaBlock;
import org.violetmoon.zeta.module.ZetaModule;

public class PaperLanternBlock extends ZetaBlock {

	private static final VoxelShape POST_SHAPE = box(6, 0, 6, 10, 16, 10);
	private static final VoxelShape LANTERN_SHAPE = box(2, 2, 2, 14, 14, 14);
	private static final VoxelShape SHAPE = Shapes.or(POST_SHAPE, LANTERN_SHAPE);

	public PaperLanternBlock(String regname, @Nullable ZetaModule module) {
		super(regname, module,
				OldMaterials.wood()
						.mapColor(MapColor.SNOW)
						.sound(SoundType.WOOD)
						.lightLevel(b -> 15)
						.strength(1.5F));

		if(module == null) //auto registration below this line
			return;
		setCreativeTab(CreativeModeTabs.BUILDING_BLOCKS);
	}

	@NotNull
	@Override
	public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return SHAPE;
	}

	@Override
	public int getFlammabilityZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 30;
	}

	@Override
	public int getFireSpreadSpeedZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 60;
	}
}
