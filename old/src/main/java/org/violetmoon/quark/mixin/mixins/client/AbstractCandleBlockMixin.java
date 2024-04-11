package org.violetmoon.quark.mixin.mixins.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import org.violetmoon.quark.content.client.module.SoulCandlesModule;

@Mixin(AbstractCandleBlock.class)
public class AbstractCandleBlockMixin {

	@WrapOperation(method = "addParticlesAndSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
	private static void addParticlesAndSound(Level instance, ParticleOptions options, double x, double y, double z, double mx, double my, double mz, Operation<Void> original) {
		ParticleOptions newOptions = SoulCandlesModule.getParticleOptions(options, instance, x, y, z);
		original.call(instance, newOptions, x, y, z, mx, my, mz);
	}

}
