package org.violetmoon.quark.mixin.mixins.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.resources.sounds.MinecartSoundInstance;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.quark.content.client.module.WoolShutsUpMinecartsModule;

@Mixin(MinecartSoundInstance.class)
public class MinecartSoundInstanceMixin {

	@WrapOperation(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;"))
	public Vec3 pretendThereIsNoMovementIfMuted(AbstractMinecart minecart, Operation<Vec3> original) {
		if (!WoolShutsUpMinecartsModule.canPlay(minecart))
			return Vec3.ZERO;
		return original.call(minecart);
	}

}
