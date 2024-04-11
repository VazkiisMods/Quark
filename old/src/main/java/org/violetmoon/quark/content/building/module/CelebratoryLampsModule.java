package org.violetmoon.quark.content.building.module;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import org.violetmoon.zeta.block.ZetaBlock;
import org.violetmoon.zeta.config.Config;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.event.play.ZItemTooltip;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

@ZetaLoadModule(category = "building")
public class CelebratoryLampsModule extends ZetaModule {

	@Config
	public static int lightLevel = 15;

	private static Block stone_lamp, stone_brick_lamp;

	@LoadEvent
	public final void register(ZRegister event) {
		stone_lamp = new ZetaBlock("stone_lamp", this, Block.Properties.copy(Blocks.STONE).lightLevel(s -> lightLevel)).setCreativeTab(CreativeModeTabs.BUILDING_BLOCKS);
		stone_brick_lamp = new ZetaBlock("stone_brick_lamp", this, Block.Properties.copy(Blocks.STONE_BRICKS).lightLevel(s -> lightLevel)).setCreativeTab(CreativeModeTabs.BUILDING_BLOCKS);
	}

	@ZetaLoadModule(clientReplacement = true)
	public static class Client extends CelebratoryLampsModule {
		@PlayEvent
		public void onTooltip(ZItemTooltip event) {
			if(event.getFlags().isAdvanced()) {
				ItemStack stack = event.getItemStack();
				Item item = stack.getItem();
				if(item == stone_lamp.asItem() || item == stone_brick_lamp.asItem())
					event.getToolTip().add(1, Component.translatable("quark.misc.celebration").withStyle(ChatFormatting.GRAY));
			}
		}
	}

}
