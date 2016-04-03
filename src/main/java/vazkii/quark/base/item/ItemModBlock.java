/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Psi Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: http://psi.vazkii.us/license.php
 *
 * File Created @ [09/01/2016, 22:42:36 (GMT)]
 */
package vazkii.quark.base.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.lib.LibMisc;

public class ItemModBlock extends ItemBlock implements IVariantHolder {

	private IQuarkBlock quarkBlock;
	
	public ItemModBlock(Block block) {
		super(block);
		quarkBlock = (IQuarkBlock) block;

		ItemMod.variantHolders.add(this);
		if(getVariants().length > 1)
			setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage) {
		return damage;	
	}

	@Override
	public ItemBlock setUnlocalizedName(String par1Str) {
//		GameRegistry.register(this);
		return super.setUnlocalizedName(par1Str);
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		int dmg = par1ItemStack.getItemDamage();
		String[] variants = getVariants();

		String name;
		if(dmg >= variants.length)
			name = quarkBlock.getBareName();
		else name = variants[dmg];

		return "tile." + LibMisc.PREFIX_MOD + name;
	}

	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		String[] variants = getVariants();
		for(int i = 0; i < variants.length; i++)
			if(quarkBlock.shouldDisplayVariant(i))
				subItems.add(new ItemStack(itemIn, 1, i));
	}

	@Override
	public String[] getVariants() {
		return quarkBlock.getVariants();
	}

	@Override
	public ItemMeshDefinition getCustomMeshDefinition() {
		return quarkBlock.getCustomMeshDefinition();
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return quarkBlock.getBlockRarity(stack);
	}

}
