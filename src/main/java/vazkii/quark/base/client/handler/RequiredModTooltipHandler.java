package vazkii.quark.base.client.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import vazkii.quark.base.handler.GeneralConfig;

public class RequiredModTooltipHandler {

	private static final Map<Item, String> ITEMS = new HashMap<>();
	private static final Map<Block, String> BLOCKS = new HashMap<>();

	public static void map(Item item, String mod) {
		ITEMS.put(item, mod);
	}

	public static void map(Block block, String mod) {
		BLOCKS.put(block, mod);
	}

	public static List<ItemStack> disabledItems() {
		if(!GeneralConfig.hideDisabledContent)
			return new ArrayList<>();
		
		return ITEMS.entrySet().stream()
				.filter((entry) -> !ModList.get().isLoaded(entry.getValue()))
				.map((entry) -> new ItemStack(entry.getKey()))
				.toList();
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onTooltip(ItemTooltipEvent event) {
		if(!BLOCKS.isEmpty() && event.getEntity() != null && event.getEntity().level != null) {
			for(Block b : BLOCKS.keySet())
				ITEMS.put(b.asItem(), BLOCKS.get(b));
			BLOCKS.clear();
		}

		Item item = event.getItemStack().getItem();
		if(ITEMS.containsKey(item)) {
			String mod = ITEMS.get(item);
			if (!ModList.get().isLoaded(mod)) {
				event.getToolTip().add(Component.translatable("quark.misc.mod_disabled", mod).withStyle(ChatFormatting.GRAY));
			}
		}
	}
}
