package org.violetmoon.quark.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import org.violetmoon.quark.content.experimental.module.VariantSelectorModule;
import org.violetmoon.quark.content.tweaks.module.LockRotationModule;

@Mixin(BlockItem.class)
public class BlockItemMixin {

	@ModifyExpressionValue(
		method = "place", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/BlockItem;getPlacementState(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/level/block/state/BlockState;"
		)
	)
	private BlockState alterPlacementState(BlockState state, @Local(ordinal = 1) BlockPlaceContext context) {
		state = VariantSelectorModule.modifyBlockPlacementState(state, context);
		return LockRotationModule.fixBlockRotation(state, context);
	}
}
