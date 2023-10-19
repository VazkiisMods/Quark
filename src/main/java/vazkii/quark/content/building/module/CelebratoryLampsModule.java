package vazkii.quark.content.building.module;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "building", hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class CelebratoryLampsModule extends QuarkModule {

	@Config
	public static int lightLevel = 15;
	
	private static Block stone_lamp, stone_brick_lamp;
	
	@LoadEvent
	public final void register(ZRegister event) {
		stone_lamp = new QuarkBlock("stone_lamp", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Block.Properties.copy(Blocks.STONE).lightLevel(s -> lightLevel));
		stone_brick_lamp = new QuarkBlock("stone_brick_lamp", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Block.Properties.copy(Blocks.STONE_BRICKS).lightLevel(s -> lightLevel));
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onTooltip(ItemTooltipEvent event) {
		if(event.getFlags().isAdvanced()) {
			ItemStack stack = event.getItemStack();
			Item item = stack.getItem();
			if(item == stone_lamp.asItem() || item == stone_brick_lamp.asItem())
				event.getToolTip().add(1, Component.translatable("quark.misc.celebration").withStyle(ChatFormatting.GRAY));
		}
	}
	
}
