package vazkii.quark.content.building.module;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.config.Config;
import vazkii.zeta.event.ZItemTooltip;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;

@ZetaLoadModule(category = "building")
public class CelebratoryLampsModule extends ZetaModule {

	@Config
	public static int lightLevel = 15;
	
	private static Block stone_lamp, stone_brick_lamp;
	
	@LoadEvent
	public final void register(ZRegister event) {
		stone_lamp = new QuarkBlock("stone_lamp", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Block.Properties.copy(Blocks.STONE).lightLevel(s -> lightLevel));
		stone_brick_lamp = new QuarkBlock("stone_brick_lamp", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Block.Properties.copy(Blocks.STONE_BRICKS).lightLevel(s -> lightLevel));
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
