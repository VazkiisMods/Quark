/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [19/03/2016, 17:25:47 (GMT)]
 */
package vazkii.quark.tweaks.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.quark.base.item.ItemMod;

public class ItemGlassShard extends ItemMod {

	private static final String[] VARIANTS = {
			"glass_shard",
			"glass_shard_white",
			"glass_shard_orange",
			"glass_shard_magenta",
			"glass_shard_light_blue",
			"glass_shard_yellow",
			"glass_shard_lime",
			"glass_shard_pink",
			"glass_shard_gray",
			"glass_shard_silver",
			"glass_shard_cyan",
			"glass_shard_purple",
			"glass_shard_blue",
			"glass_shard_brown",
			"glass_shard_green",
			"glass_shard_red",
			"glass_shard_black"
	};

	public ItemGlassShard() {
		super("glass_shards", VARIANTS);
		setCreativeTab(CreativeTabs.tabMaterials);
		OreDictionary.registerOre("shardGlass", this);
	}

}
