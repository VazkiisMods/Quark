package org.violetmoon.quark.mixin.mixins.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.world.level.biome.Biome;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.client.module.GreenerGrassModule;

@Mixin(Biome.class)
public class BiomeMixin {

	@ModifyReturnValue(method = { "getWaterColor", "getWaterFogColor" }, at = @At("RETURN"))
	private int getWaterColor(int prev) {
		return Quark.ZETA.modules.get(GreenerGrassModule.class).getWaterColor(prev);
	}

}
