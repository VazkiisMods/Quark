package org.violetmoon.quark.content.building.block.be;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import org.violetmoon.quark.content.building.module.VariantChestsModule;

public class VariantChestBlockEntity extends ChestBlockEntity {

	protected VariantChestBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(typeIn, pos, state);
	}

	public VariantChestBlockEntity(BlockPos pos, BlockState state) {
		super(VariantChestsModule.chestTEType, pos, state);
	}

	@Override
	public AABB getRenderBoundingBox() {
		return new AABB(worldPosition.offset(-1, 0, -1), worldPosition.offset(2, 2, 2));
	}

}
