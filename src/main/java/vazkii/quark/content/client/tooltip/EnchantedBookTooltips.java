package vazkii.quark.content.client.tooltip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import vazkii.quark.base.Quark;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.content.client.module.ImprovedTooltipsModule;
import vazkii.quark.content.tools.item.AncientTomeItem;
import vazkii.quark.content.tools.module.AncientTomesModule;
import vazkii.zeta.client.event.ZGatherTooltipComponents;
import vazkii.zeta.util.ItemNBTHelper;

public class EnchantedBookTooltips {

	private static List<ItemStack> testItems = null;
	private static Multimap<Enchantment, ItemStack> additionalStacks = null;

	public static final String TABLE_ONLY_DISPLAY = "quark:only_show_table_enchantments";

	public static void reloaded() {
		additionalStacks = null;
		testItems = null;
	}

	public static void makeTooltip(ZGatherTooltipComponents event) {
		Minecraft mc = Minecraft.getInstance();
		if(mc.player == null)
			return;

		ItemStack stack = event.getItemStack();
		if(stack.getItem() == Items.ENCHANTED_BOOK || stack.getItem() == AncientTomesModule.ancient_tome) {
			List<Either<FormattedText, TooltipComponent>> tooltip = event.getTooltipElements();
			int tooltipIndex = 0;

			List<EnchantmentInstance> enchants = getEnchantedBookEnchantments(stack);
			for(EnchantmentInstance ed : enchants) {
				Component match;
				if (stack.getItem() == Items.ENCHANTED_BOOK)
					match = ed.enchantment.getFullname(ed.level);
				else
					match = AncientTomeItem.getFullTooltipText(ed.enchantment);

				for(; tooltipIndex < tooltip.size(); tooltipIndex++) {
					Either<FormattedText, TooltipComponent> elmAt = tooltip.get(tooltipIndex);
					if(elmAt.left().isPresent() && elmAt.left().get().equals(match)) {
						boolean tableOnly = ItemNBTHelper.getBoolean(stack, TABLE_ONLY_DISPLAY, false);
						List<ItemStack> items = getItemsForEnchantment(ed.enchantment, tableOnly);
						int itemCount = items.size();
						int lines = (int) Math.ceil((double) itemCount / 10.0);

						int len = 3 + Math.min(10, itemCount) * 9;
						tooltip.add(tooltipIndex + 1, Either.right(new EnchantedBookComponent(len, lines * 10, ed.enchantment, tableOnly)));

						break;
					}
				}
			}
		}
	}

	private static ItemStack BOOK;

	private static List<ItemStack> getItemsForEnchantment(Enchantment e, boolean onlyForTable) {
		List<ItemStack> list = new ArrayList<>();

		for(ItemStack stack : getTestItems()) {
			Item item = stack.getItem();
			if(item instanceof QuarkItem && !((QuarkItem) item).isEnabled())
				continue;

			if(!stack.isEmpty() && e.canEnchant(stack)) {
				if (onlyForTable && (!e.canApplyAtEnchantingTable(stack) || !stack.isEnchantable() || Quark.ZETA.itemExtensions.get(stack).getEnchantmentValueZeta(stack) <= 0))
					continue;
				list.add(stack);
			}
		}

		if (onlyForTable) {
			if (BOOK == null)
				BOOK = new ItemStack(Items.BOOK);
			list.add(BOOK);
		}

		if(getAdditionalStacks().containsKey(e))
			list.addAll(getAdditionalStacks().get(e));

		return list;
	}

	private static List<EnchantmentInstance> getEnchantedBookEnchantments(ItemStack stack) {
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

		List<EnchantmentInstance> retList = new ArrayList<>(enchantments.size());

		for(Enchantment enchantment : enchantments.keySet()) {
			if (enchantment != null) {
				int level = enchantments.get(enchantment);
				retList.add(new EnchantmentInstance(enchantment, level));
			}
		}

		return retList;
	}

	private static Multimap<Enchantment, ItemStack> getAdditionalStacks() {
		if (additionalStacks == null)
			computeAdditionalStacks();
		return additionalStacks;
	}

	public static List<ItemStack> getTestItems() {
		if (testItems == null)
			computeTestItems();
		return testItems;
	}

	private static void computeTestItems() {
		testItems = ImprovedTooltipsModule.enchantingStacks.stream()
			.map(ResourceLocation::new)
			.map(Registry.ITEM::get)
			.filter(i -> i != Items.AIR)
			.map(ItemStack::new)
			.toList();
	}

	private static void computeAdditionalStacks() {
		additionalStacks = HashMultimap.create();

		for(String s : ImprovedTooltipsModule.enchantingAdditionalStacks) {
			if(!s.contains("="))
				continue;

			String[] tokens = s.split("=");
			String left = tokens[0];
			String right = tokens[1];

			Registry.ENCHANTMENT.getOptional(new ResourceLocation(left))
				.ifPresent(ench -> {
					for(String itemId : right.split(",")) {
						Registry.ITEM.getOptional(new ResourceLocation(itemId)).ifPresent(item ->
							additionalStacks.put(ench, new ItemStack(item)));
					}
				});
		}
	}

	public record EnchantedBookComponent(int width, int height,
										 Enchantment enchantment, boolean tableOnly) implements ClientTooltipComponent, TooltipComponent {

		@Override
		public void renderImage(@Nonnull Font font, int tooltipX, int tooltipY, @Nonnull PoseStack basePose, @Nonnull ItemRenderer itemRenderer, int something) {
			PoseStack modelviewPose = RenderSystem.getModelViewStack();

			modelviewPose.pushPose();
			modelviewPose.translate(tooltipX, tooltipY, 0);
			modelviewPose.scale(0.5f, 0.5f, 1.0f);
			Minecraft mc = Minecraft.getInstance();
			List<ItemStack> items = getItemsForEnchantment(enchantment, tableOnly);
			int drawn = 0;
			for (ItemStack testStack : items) {
				mc.getItemRenderer().renderGuiItem(testStack, 6 + (drawn % 10) * 18, (drawn / 10) * 20);
				drawn++;
			}

			modelviewPose.popPose();
			RenderSystem.applyModelViewMatrix();
		}

		@Override
		public int getHeight() {
			return height;
		}

		@Override
		public int getWidth(@Nonnull Font font) {
			return width;
		}

	}
}
