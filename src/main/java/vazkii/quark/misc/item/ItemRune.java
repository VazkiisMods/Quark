/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [19/06/2016, 03:03:47 (GMT)]
 */
package vazkii.quark.misc.item;

import java.awt.Color;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.interf.IItemColorProvider;
import vazkii.arl.item.ItemMod;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.api.ICustomEnchantColor;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.misc.feature.ColorRunes;

public class ItemRune extends ItemMod implements IItemColorProvider, ICustomEnchantColor, IQuarkItem {

	private static final String[] VARIANTS = {
			"rune_white",
			"rune_orange",
			"rune_magenta",
			"rune_light_blue",
			"rune_yellow",
			"rune_lime",
			"rune_pink",
			"rune_gray",
			"rune_silver",
			"rune_cyan",
			"rune_purple",
			"rune_blue",
			"rune_brown",
			"rune_green",
			"rune_red",
			"rune_black",
			"rune_rainbow"
	};

	public ItemRune(boolean stack) {
		super("rune", VARIANTS);
		if(!stack)
			setMaxStackSize(1);
		setCreativeTab(CreativeTabs.MISC);
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public String getUniqueModel() {
		return "rune";
	}

	@Override
	public int getEnchantEffectColor(ItemStack stack) {
		return getColor(stack.getItemDamage());
	}
	
	public static int getColor(int meta) {
		if(meta > 15) 
			return Color.HSBtoRGB(ClientTicker.total * 0.005F, 1F, 0.6F);
		
		else return ItemDye.DYE_COLORS[15 - meta];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IItemColor getItemColor() {
		return new IItemColor() {

			@Override
			public int colorMultiplier(ItemStack stack, int tintIndex) {
				return tintIndex == 1 ? ColorRunes.getColorFromStack(stack) : 0xFFFFFF;
			}
		};
	}

}
