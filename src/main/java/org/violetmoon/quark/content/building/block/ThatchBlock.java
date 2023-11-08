package org.violetmoon.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.violetmoon.quark.base.block.QuarkFlammableBlock;
import org.violetmoon.quark.content.building.module.ThatchModule;
import org.violetmoon.zeta.module.ZetaModule;

import javax.annotation.Nonnull;

public class ThatchBlock extends QuarkFlammableBlock {

	public ThatchBlock(ZetaModule module) {
		super("thatch", module, CreativeModeTab.TAB_BUILDING_BLOCKS, 300,
				Block.Properties.of(Material.GRASS, MaterialColor.COLOR_YELLOW)
				.strength(0.5F)
				.sound(SoundType.GRASS));
	}

	@Override
	public void fallOn(@Nonnull Level worldIn, @Nonnull BlockState state, @Nonnull BlockPos pos, Entity entityIn, float fallDistance) {
		entityIn.causeFallDamage(fallDistance, (float) ThatchModule.fallDamageMultiplier, DamageSource.FALL);
	}

}
