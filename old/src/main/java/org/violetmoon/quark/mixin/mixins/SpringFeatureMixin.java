package org.violetmoon.quark.mixin.mixins;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.SpringFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.violetmoon.quark.content.world.module.NoMoreLavaPocketsModule;

@Mixin(SpringFeature.class)
public class SpringFeatureMixin {

	@Inject(method = "place", at = @At("HEAD"), cancellable = true)
	private void canSurvive(FeaturePlaceContext<SpringConfiguration> context, CallbackInfoReturnable<Boolean> cir) {
		if(NoMoreLavaPocketsModule.shouldDisable(context.config()))
			cir.setReturnValue(false);
	}

}
