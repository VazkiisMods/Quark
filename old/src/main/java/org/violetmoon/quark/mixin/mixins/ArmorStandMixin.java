package org.violetmoon.quark.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.quark.content.tweaks.module.ArmedArmorStandsModule;

@Mixin(ArmorStand.class)
public class ArmorStandMixin {
	@ModifyExpressionValue(method = "defineSynchedData", at = @At(value = "CONSTANT", args = "intValue=0"))
	private int quark$armedArmorStands(int original) {
		if(!ArmedArmorStandsModule.staticEnabled)
			return original;

		//try to be careful - this is a kinda scary looking mixin, lol
		SynchedEntityData data = ((Entity) (Object) this).getEntityData();
		if(data.hasItem(ArmorStand.DATA_CLIENT_FLAGS))
			return original; //it's already been defined

		else
			return original | ArmorStand.CLIENT_FLAG_SHOW_ARMS; // | 4
	}
}
