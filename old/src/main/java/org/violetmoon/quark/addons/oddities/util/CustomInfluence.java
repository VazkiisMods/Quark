package org.violetmoon.quark.addons.oddities.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import org.violetmoon.quark.api.IEnchantmentInfluencer;

public record CustomInfluence(int strength, int color, Influence influence) implements IEnchantmentInfluencer {
	@Override
	public float[] getEnchantmentInfluenceColor(BlockGetter world, BlockPos pos, BlockState state) {
		float r = FastColor.ARGB32.red(color) / 255f;
		float g = FastColor.ARGB32.green(color) / 255f;
		float b = FastColor.ARGB32.blue(color) / 255f;
		return new float[] { r, g, b };
	}

	@Override
	public int getInfluenceStack(BlockGetter world, BlockPos pos, BlockState state) {
		return strength;
	}

	@Override
	public boolean influencesEnchantment(BlockGetter world, BlockPos pos, BlockState state, Enchantment enchantment) {
		return influence.boost().contains(enchantment);
	}

	@Override
	public boolean dampensEnchantment(BlockGetter world, BlockPos pos, BlockState state, Enchantment enchantment) {
		return influence.dampen().contains(enchantment);
	}
}
