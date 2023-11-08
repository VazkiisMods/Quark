package org.violetmoon.quark.content.tools.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.violetmoon.quark.base.item.QuarkItem;
import org.violetmoon.quark.content.experimental.module.EnchantmentsBegoneModule;
import org.violetmoon.quark.content.tools.module.AncientTomesModule;
import org.violetmoon.zeta.module.ZetaModule;

import javax.annotation.Nonnull;
import java.util.List;

public class AncientTomeItem extends QuarkItem {

	public AncientTomeItem(ZetaModule module) {
		super("ancient_tome", module,
				new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
	}

	@Override
	public boolean isEnchantable(@Nonnull ItemStack stack) {
		return false;
	}

	@Override
	public boolean isFoil(@Nonnull ItemStack stack) {
		return true;
	}

	//TODO: IForgeItem
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return false;
	}

	public static ItemStack getEnchantedItemStack(Enchantment ench) {
		ItemStack newStack = new ItemStack(AncientTomesModule.ancient_tome);
		EnchantedBookItem.addEnchantment(newStack, new EnchantmentInstance(ench, ench.getMaxLevel()));
		return newStack;
	}

	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if (isEnabled() || group == CreativeModeTab.TAB_SEARCH) {
			if (group == CreativeModeTab.TAB_SEARCH || group.getEnchantmentCategories().length != 0) {
				Registry.ENCHANTMENT.forEach(ench -> {
					if (!EnchantmentsBegoneModule.shouldBegone(ench) && (!AncientTomesModule.sanityCheck || ench.getMaxLevel() != 1)) {
						if (!AncientTomesModule.isInitialized() || AncientTomesModule.validEnchants.contains(ench)) {
							if (group == CreativeModeTab.TAB_SEARCH || group.hasEnchantmentCategory(ench.category)) {
								items.add(getEnchantedItemStack(ench));
							}
						}
					}
				});
			}
		}
	}

	public static Component getFullTooltipText(Enchantment ench) {
		return Component.translatable("quark.misc.ancient_tome_tooltip", Component.translatable(ench.getDescriptionId()), Component.translatable("enchantment.level." + (ench.getMaxLevel() + 1))).withStyle(ChatFormatting.GRAY);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(@Nonnull ItemStack stack, Level worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);

		Enchantment ench = AncientTomesModule.getTomeEnchantment(stack);
		if(ench != null)
			tooltip.add(getFullTooltipText(ench));
		else
			tooltip.add(Component.translatable("quark.misc.ancient_tome_tooltip_any").withStyle(ChatFormatting.GRAY));

		if(AncientTomesModule.curseGear){
			tooltip.add(Component.translatable("quark.misc.ancient_tome_tooltip_curse").withStyle(ChatFormatting.RED));
		}
	}

}
