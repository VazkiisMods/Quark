package org.violetmoon.quark.mixin.mixins;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.world.level.biome.Climate;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import org.violetmoon.quark.content.experimental.module.ClimateControlRemoverModule;

import java.util.ArrayList;
import java.util.List;

@Mixin(Climate.ParameterPoint.class)
public class ClimateParameterPointMixin {

	@Shadow
	@Final
	private Climate.Parameter temperature;
	@Shadow
	@Final
	private Climate.Parameter humidity;
	@Shadow
	@Final
	private Climate.Parameter continentalness;
	@Shadow
	@Final
	private Climate.Parameter erosion;
	@Shadow
	@Final
	private Climate.Parameter depth;
	@Shadow
	@Final
	private Climate.Parameter weirdness;

	@Shadow
	@Final
	private long offset;

	@WrapOperation(method = "fitness", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Climate$Parameter;distance(J)J"))
	public long giveMinimumDistanceForDisabledParameters(Climate.Parameter parameter, long targetValue, Operation<Long> original) {
		if(ClimateControlRemoverModule.staticEnabled &&
				((parameter == temperature && ClimateControlRemoverModule.disableTemperature) ||
						(parameter == humidity && ClimateControlRemoverModule.disableHumidity) ||
						(parameter == continentalness && ClimateControlRemoverModule.disableContinentalness) ||
						(parameter == erosion && ClimateControlRemoverModule.disableErosion) ||
						(parameter == depth && ClimateControlRemoverModule.disableDepth) ||
						(parameter == weirdness && ClimateControlRemoverModule.disableWeirdness)))
			return 0;

		return original.call(parameter, targetValue);
	}

	@ModifyExpressionValue(method = "fitness", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/biome/Climate$ParameterPoint;offset:J", opcode = Opcodes.GETFIELD))
	public long giveMinimumOffsetIfDisabled(long originalOffset) {
		if(ClimateControlRemoverModule.staticEnabled && ClimateControlRemoverModule.disableOffset)
			return 0;
		return originalOffset;
	}

	@ModifyReturnValue(method = "parameterSpace", at = @At("RETURN"))
	public List<Climate.Parameter> dummyOutDisabledParameters(List<Climate.Parameter> original) {
		if(ClimateControlRemoverModule.staticEnabled) {
			Climate.Parameter dummyParameter = new Climate.Parameter(0, 0);

			List<Climate.Parameter> newParameterSpace = new ArrayList<>(original.size());

			for(Climate.Parameter parameter : original) {
				if(parameter == humidity) {
					if(ClimateControlRemoverModule.disableHumidity)
						newParameterSpace.add(dummyParameter);
					else
						newParameterSpace.add(parameter);
				} else if(parameter == temperature) {
					if(ClimateControlRemoverModule.disableTemperature)
						newParameterSpace.add(dummyParameter);
					else
						newParameterSpace.add(parameter);
				} else if(parameter == continentalness) {
					if(ClimateControlRemoverModule.disableContinentalness)
						newParameterSpace.add(dummyParameter);
					else
						newParameterSpace.add(parameter);
				} else if(parameter == erosion) {
					if(ClimateControlRemoverModule.disableErosion)
						newParameterSpace.add(dummyParameter);
					else
						newParameterSpace.add(parameter);
				} else if(parameter == depth) {
					if(ClimateControlRemoverModule.disableDepth)
						newParameterSpace.add(dummyParameter);
					else
						newParameterSpace.add(parameter);
				} else if(parameter == weirdness) {
					if(ClimateControlRemoverModule.disableWeirdness)
						newParameterSpace.add(dummyParameter);
					else
						newParameterSpace.add(parameter);
				} else if(parameter.min() == parameter.max() && parameter.min() == offset && ClimateControlRemoverModule.disableOffset) {
					newParameterSpace.add(dummyParameter);
				} else {
					newParameterSpace.add(parameter);
				}
			}

			return ImmutableList.copyOf(newParameterSpace);
		}

		return original;
	}

}
