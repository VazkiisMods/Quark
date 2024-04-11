package org.violetmoon.quark.mixin.mixins.client;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import org.violetmoon.quark.content.experimental.module.VariantSelectorModule;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

	@ModifyVariable(method = "renderArmWithItem", at = @At("HEAD"), argsOnly = true)
	private ItemStack renderArmWithItem(ItemStack stack, AbstractClientPlayer player) {
		return VariantSelectorModule.Client.modifyHeldItemStack(player, stack);
	}

}
