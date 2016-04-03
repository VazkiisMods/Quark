/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Psi Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: http://psi.vazkii.us/license.php
 *
 * File Created @ [08/01/2016, 21:47:43 (GMT)]
 */
package vazkii.quark.base.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.quark.base.handler.TooltipHandler;
import vazkii.quark.base.lib.LibMisc;

public class ItemMod extends Item implements IVariantHolder {

	public static final List<IVariantHolder> variantHolders = new ArrayList();

	private final String[] variants;
	private final String bareName;

	public ItemMod(String name, String... variants) {
		setUnlocalizedName(name);
		if(variants.length > 1)
			setHasSubtypes(true);

		if(variants.length == 0)
			variants = new String[] { name };

		bareName = name;
		this.variants = variants;
		variantHolders.add(this);
	}

	@Override
	public Item setUnlocalizedName(String name) {
		super.setUnlocalizedName(name);
		GameRegistry.register(this, new ResourceLocation(LibMisc.PREFIX_MOD + name));

		return this;
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		int dmg = par1ItemStack.getItemDamage();
		String[] variants = getVariants();

		String name;
		if(dmg >= variants.length)
			name = bareName;
		else name = variants[dmg];

		return "item." + LibMisc.PREFIX_MOD + name;
	}

	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		for(int i = 0; i < getVariants().length; i++)
			subItems.add(new ItemStack(itemIn, 1, i));
	}

	@Override
	public String[] getVariants() {
		return variants;
	}

	@Override
	public ItemMeshDefinition getCustomMeshDefinition() {
		return null;
	}

	public static void tooltipIfShift(List<String> tooltip, Runnable r) {
		TooltipHandler.tooltipIfShift(tooltip, r);
	}

	public static void addToTooltip(List<String> tooltip, String s, Object... format) {
		TooltipHandler.addToTooltip(tooltip, s, format);
	}

	public static String local(String s) {
		return TooltipHandler.local(s);
	}

}
