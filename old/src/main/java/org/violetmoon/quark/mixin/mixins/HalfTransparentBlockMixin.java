package org.violetmoon.quark.mixin.mixins;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.violetmoon.quark.addons.oddities.module.PipesModule;

@Mixin(HalfTransparentBlock.class)
public class HalfTransparentBlockMixin {

	@Inject(method = "skipRendering", at = @At("HEAD"), cancellable = true)
	private void skipRendering(BlockState state, BlockState other, Direction direction, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		if(state.is(Blocks.GLASS) && other.is(PipesModule.encasedPipe)) {
			callbackInfoReturnable.setReturnValue(true);
			callbackInfoReturnable.cancel();
		}
	}

}
