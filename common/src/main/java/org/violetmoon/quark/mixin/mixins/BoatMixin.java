package org.violetmoon.quark.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.world.entity.vehicle.Boat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import org.violetmoon.quark.content.experimental.module.GameNerfsModule;

@Mixin(Boat.class)
public class BoatMixin {

	@ModifyReturnValue(method = "getGroundFriction", at = @At("RETURN"))
	private float getGroundFriction(float prev) {
		return GameNerfsModule.getBoatFriction(prev);
	}

}
