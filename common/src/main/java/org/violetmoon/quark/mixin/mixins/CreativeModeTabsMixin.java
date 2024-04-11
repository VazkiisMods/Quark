package org.violetmoon.quark.mixin.mixins;

import java.util.stream.Stream;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.quark.content.experimental.module.EnchantmentsBegoneModule;

@Mixin(CreativeModeTabs.class)
public class CreativeModeTabsMixin {

	@ModifyExpressionValue(method = "generateEnchantmentBookTypesOnlyMaxLevel", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", ordinal = 0))
	private static Stream<Enchantment> quark$filterEnchantments(Stream<Enchantment> in) {
		return in.filter(ench -> !EnchantmentsBegoneModule.shouldBegone(ench));
	}

	@ModifyExpressionValue(method = "generateEnchantmentBookTypesAllLevels", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", ordinal = 0))
	private static Stream<Enchantment> quark$filterEnchantments2(Stream<Enchantment> in) {
		return in.filter(ench -> !EnchantmentsBegoneModule.shouldBegone(ench));
	}

}
