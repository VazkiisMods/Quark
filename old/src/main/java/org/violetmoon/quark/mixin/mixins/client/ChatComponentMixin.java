package org.violetmoon.quark.mixin.mixins.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.FormattedCharSequence;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import org.violetmoon.quark.content.management.module.ItemSharingModule;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
	@WrapOperation(
		method = "render", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)I"
		)
	)
	private int drawItems(GuiGraphics instance, Font font, FormattedCharSequence formattedCharSequence, int x, int y, int color, Operation<Integer> original) {
		ItemSharingModule.Client.renderItemForMessage(instance, formattedCharSequence, x, y, color);

		return original.call(instance, font, formattedCharSequence, x, y, color);
	}
}
