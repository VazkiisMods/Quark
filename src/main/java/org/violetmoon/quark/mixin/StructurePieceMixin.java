package org.violetmoon.quark.mixin;

import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.violetmoon.quark.base.handler.StructureBlockReplacementHandler;

@Mixin(StructurePiece.class)
public class StructurePieceMixin {

	@ModifyVariable(
			method = "createChest(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/block/state/BlockState;)Z",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerLevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", 
			shift = At.Shift.BY, by = -2),
			argsOnly = true
			)
	protected BlockState modifyBlockstateForChest(BlockState state, ServerLevelAccessor accessor) {
		return StructureBlockReplacementHandler.getResultingBlockState(accessor, state);
	}

	@ModifyVariable(
			method = "placeBlock(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/block/state/BlockState;IIILnet/minecraft/world/level/levelgen/structure/BoundingBox;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", 
			shift = At.Shift.BY, by = -2),
			argsOnly = true
			)
	protected BlockState modifyBlockstate(BlockState state, WorldGenLevel level) {
		return StructureBlockReplacementHandler.getResultingBlockState(level, state);
	}

}
