package org.violetmoon.quark.content.tweaks.module;

import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LayeredCauldronBlock;

import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

@ZetaLoadModule(category = "tweaks", antiOverlap = "supplementaries")
public class MapWashingModule extends ZetaModule {
	private final CauldronInteraction WASHING_MAP = (state, level, pos, player, hand, stack) -> {
		if(!enabled)
			return InteractionResult.PASS;

		if(!stack.is(Items.FILLED_MAP)) {
			return InteractionResult.PASS;
		} else {
			if(!level.isClientSide) {
				player.setItemInHand(hand, new ItemStack(Items.MAP, stack.getCount()));
				LayeredCauldronBlock.lowerFillLevel(state, level, pos);
			}

			return InteractionResult.sidedSuccess(level.isClientSide);
		}
	};

	@LoadEvent
	public final void setup(ZCommonSetup event) {
		CauldronInteraction.WATER.put(Items.FILLED_MAP, WASHING_MAP);
	}
}
