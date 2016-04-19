/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 * 
 * File Created @ [18/04/2016, 17:37:41 (GMT)]
 */
package vazkii.quark.world.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.quark.base.item.ItemMod;

public class ItemBiotite extends ItemMod {

	public ItemBiotite() {
		super("biotite");
		OreDictionary.registerOre("gemEnderBiotite", this);
		setCreativeTab(CreativeTabs.tabMaterials);
	}

}
