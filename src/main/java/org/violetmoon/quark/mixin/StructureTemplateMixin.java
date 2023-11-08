package org.violetmoon.quark.mixin;

import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.violetmoon.quark.base.handler.StructureBlockReplacementHandler;

@Mixin(StructureTemplate.class)
public class StructureTemplateMixin {

	@ModifyVariable(method = "placeInWorld(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Lnet/minecraft/util/RandomSource;I)Z",
			at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$StructureBlockInfo;nbt:Lnet/minecraft/nbt/CompoundTag;", ordinal = 0),
			index = 22)
	private BlockState captureLocalBlockstate(BlockState state, ServerLevelAccessor accessor) {
		return StructureBlockReplacementHandler.getResultingBlockState(accessor, state);
	}
}
