package org.violetmoon.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.violetmoon.quark.content.building.module.ThatchModule;
import org.violetmoon.zeta.block.OldMaterials;
import org.violetmoon.zeta.block.ZetaFlammableBlock;
import org.violetmoon.zeta.module.ZetaModule;

public class ThatchBlock extends ZetaFlammableBlock {

	public ThatchBlock(@Nullable ZetaModule module) {
		super("thatch", module, 300,
				OldMaterials.grass()
						.mapColor(MapColor.COLOR_YELLOW)
						.strength(0.5F)
						.sound(SoundType.GRASS));

		if(module == null) //auto registration below this line
			return;
		setCreativeTab(CreativeModeTabs.BUILDING_BLOCKS);
	}

	@Override
	public void fallOn(@NotNull Level worldIn, @NotNull BlockState state, @NotNull BlockPos pos, Entity entityIn, float fallDistance) {
		entityIn.causeFallDamage(fallDistance, (float) ThatchModule.fallDamageMultiplier, entityIn.damageSources().fall());
	}

}
