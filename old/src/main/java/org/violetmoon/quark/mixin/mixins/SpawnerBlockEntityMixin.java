package org.violetmoon.quark.mixin.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.violetmoon.quark.content.experimental.module.SpawnerReplacerModule;

@Mixin(SpawnerBlockEntity.class)
public class SpawnerBlockEntityMixin {
	@Inject(method = "serverTick", at = @At("HEAD"))
	private static void onServerTick(Level level, BlockPos pos, BlockState state, SpawnerBlockEntity be, CallbackInfo ci) {
		SpawnerReplacerModule.spawnerUpdate(level, pos, state, be);
	}
}
