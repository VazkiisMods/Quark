package org.violetmoon.quark.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.quark.content.automation.module.PistonsMoveTileEntitiesModule;
import org.violetmoon.quark.content.experimental.module.GameNerfsModule;

@Mixin(PistonMovingBlockEntity.class)
public class PistonMovingBlockEntityMixin {

	@WrapOperation(method = { "tick", "finalTick" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
	private static boolean tick(Level instance, BlockPos pos, BlockState newState, int flags, Operation<Boolean> original) {
		return PistonsMoveTileEntitiesModule.setPistonBlock(instance, pos, newState, flags) || original.call(instance, pos, newState, flags);
	}

	@ModifyExpressionValue(method = "tick", at = @At(value = "CONSTANT", args = "intValue=84"))
	private static int forceNotifyBlockUpdate(int original) {
		// paper impl comment: Paper - force notify (original 2),
		// it's possible the set type by the piston block (which doesn't notify) to set this block to air
		return GameNerfsModule.stopPistonPhysicsExploits() ? (original | 2) : original;
	}
}
