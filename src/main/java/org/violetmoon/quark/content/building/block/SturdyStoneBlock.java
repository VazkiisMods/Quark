package org.violetmoon.quark.content.building.block;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import org.violetmoon.quark.base.block.QuarkBlock;
import org.violetmoon.zeta.module.ZetaModule;

import javax.annotation.Nonnull;

public class SturdyStoneBlock extends QuarkBlock {

	public SturdyStoneBlock(ZetaModule module) {
		super("sturdy_stone", module, CreativeModeTab.TAB_BUILDING_BLOCKS,
				Block.Properties.of(Material.STONE)
				.requiresCorrectToolForDrops()
				.strength(4F, 10F)
				.sound(SoundType.STONE));
	}

	@Nonnull
	@Override
	public PushReaction getPistonPushReaction(@Nonnull BlockState state) {
		return PushReaction.BLOCK;
	}

}
