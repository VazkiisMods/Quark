package org.violetmoon.quark.mixin.mixins.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.client.gui.screens.Screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import org.violetmoon.quark.content.management.module.EasyTransferingModule;

@Mixin(Screen.class)
public class ScreenMixin {

	@ModifyReturnValue(method = "hasShiftDown", at = @At("RETURN"))
	private static boolean hasShiftDown(boolean prev) {
		return EasyTransferingModule.Client.hasShiftDown(prev);
	}
}
