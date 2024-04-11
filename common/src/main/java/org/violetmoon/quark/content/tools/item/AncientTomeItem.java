package org.violetmoon.quark.content.tools.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import org.violetmoon.quark.content.experimental.module.EnchantmentsBegoneModule;
import org.violetmoon.quark.content.tools.module.AncientTomesModule;
import org.violetmoon.zeta.item.ZetaItem;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.CreativeTabManager;

import java.util.ArrayList;
import java.util.List;

public class AncientTomeItem extends ZetaItem implements CreativeTabManager.AppendsUniquely {

	public AncientTomeItem(ZetaModule module) {
		super("ancient_tome", module,
				new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
		CreativeTabManager.addToCreativeTab(CreativeModeTabs.INGREDIENTS, this);
	}

	@Override
	public boolean isEnchantable(@NotNull ItemStack stack) {
		return false;
	}

	@Override
	public boolean isFoil(@NotNull ItemStack stack) {
		return true;
	}

	@Override
	public boolean canApplyAtEnchantingTableZeta(ItemStack stack, Enchantment enchantment) {
		return false;
	}

	public static ItemStack getEnchantedItemStack(Enchantment ench) {
		ItemStack newStack = new ItemStack(AncientTomesModule.ancient_tome);
		EnchantedBookItem.addEnchantment(newStack, new EnchantmentInstance(ench, ench.getMaxLevel()));
		return newStack;
	}

	public static Component getFullTooltipText(Enchantment ench) {
		return Component.translatable("quark.misc.ancient_tome_tooltip", Component.translatable(ench.getDescriptionId()), Component.translatable("enchantment.level." + (ench.getMaxLevel() + 1))).withStyle(ChatFormatting.GRAY);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);

		Enchantment ench = AncientTomesModule.getTomeEnchantment(stack);
		if(ench != null)
			tooltip.add(getFullTooltipText(ench));
		else
			tooltip.add(Component.translatable("quark.misc.ancient_tome_tooltip_any").withStyle(ChatFormatting.GRAY));

		if(AncientTomesModule.curseGear) {
			tooltip.add(Component.translatable("quark.misc.ancient_tome_tooltip_curse").withStyle(ChatFormatting.RED));
		}
	}

	@Override
	public List<ItemStack> appendItemsToCreativeTab() {
		List<ItemStack> items = new ArrayList<>();
		BuiltInRegistries.ENCHANTMENT.forEach(ench -> {
			if(!EnchantmentsBegoneModule.shouldBegone(ench) && (!AncientTomesModule.sanityCheck || ench.getMaxLevel() != 1)) {
				if(!AncientTomesModule.isInitialized() || AncientTomesModule.validEnchants.contains(ench)) {
					items.add(getEnchantedItemStack(ench));
				}
			}
		});
		return items;
	}
}
