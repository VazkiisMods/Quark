package org.violetmoon.quark.base.client.handler;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModList;
import org.violetmoon.quark.base.handler.GeneralConfig;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.event.play.ZItemTooltip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public static class Client {
		private final Zeta z;

		public Client(Zeta z) {
			this.z = z;
		}

		@PlayEvent
		public void onTooltip(ZItemTooltip event) {
			if(!BLOCKS.isEmpty() && event.getEntity() != null && event.getEntity().level != null) {
				for(Block b : BLOCKS.keySet())
					ITEMS.put(b.asItem(), BLOCKS.get(b));
				BLOCKS.clear();
			}

			Item item = event.getItemStack().getItem();
			if(ITEMS.containsKey(item)) {
				String mod = ITEMS.get(item);
				if (!z.isModLoaded(mod)) {
					event.getToolTip().add(Component.translatable("quark.misc.mod_disabled", mod).withStyle(ChatFormatting.GRAY));
				}
			}
		}
	}
}
