package org.violetmoon.quark.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import org.violetmoon.quark.content.building.module.VerticalSlabsModule;

@Mixin(IronBarsBlock.class)
public class IronBarsBlockMixin {

	@ModifyReturnValue(method = "getStateForPlacement", at = @At("RETURN"))
	private BlockState connectsTo(BlockState prev, BlockPlaceContext context) {
		return VerticalSlabsModule.messWithPaneState(context.getLevel(), context.getClickedPos(), prev);
	}

	@ModifyReturnValue(method = "updateShape", at = @At("RETURN"))
	private BlockState updateShape(BlockState prev, BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		return VerticalSlabsModule.messWithPaneState(level, pos, prev);
	}

}
