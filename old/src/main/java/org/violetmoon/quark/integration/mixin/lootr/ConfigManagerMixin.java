package org.violetmoon.quark.integration.mixin.lootr;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import org.violetmoon.quark.base.Quark;

import noobanidus.mods.lootr.config.ConfigManager;

@Pseudo
@Mixin(value = ConfigManager.class, remap = false)
public class ConfigManagerMixin {
	@ModifyVariable(method = "addSafeReplacement", at = @At("HEAD"), argsOnly = true, remap = false)
	private static Block replacement(Block original, ResourceLocation location) {
		Block block = BuiltInRegistries.BLOCK.get(location);
		if(block != Blocks.AIR) {
			Block lootrVariant = Quark.LOOTR_INTEGRATION.lootrVariant(block);
			if(lootrVariant != null)
				return lootrVariant;
		}

		return original;
	}
}
