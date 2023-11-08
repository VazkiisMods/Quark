package org.violetmoon.quark.content.tweaks.module;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.block.QuarkBlock;
import org.violetmoon.quark.base.block.QuarkInheritedPaneBlock;
import org.violetmoon.quark.base.item.QuarkItem;
import org.violetmoon.quark.content.tweaks.block.DirtyGlassBlock;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

import java.util.HashMap;
import java.util.Map;

/**
 * @author WireSegal
 * Created at 12:26 PM on 8/24/19.
 */
@ZetaLoadModule(category = "tweaks")
public class GlassShardModule extends ZetaModule {

	public static QuarkBlock dirtyGlass;

	public static TagKey<Item> shardTag;

	public static Item clearShard;
	public static Item dirtyShard;

	public static final Map<DyeColor, Item> shardColors = new HashMap<>();

	@LoadEvent
	public final void register(ZRegister event) {
		dirtyGlass = new DirtyGlassBlock("dirty_glass", this, CreativeModeTab.TAB_BUILDING_BLOCKS,
				Block.Properties.of(Material.GLASS, MaterialColor.COLOR_BROWN).strength(0.3F).sound(SoundType.GLASS));
		new QuarkInheritedPaneBlock(dirtyGlass);

		clearShard = new QuarkItem("clear_shard", this, new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS));
		dirtyShard = new QuarkItem("dirty_shard", this, new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS));

		for(DyeColor color : DyeColor.values())
			shardColors.put(color, new QuarkItem(color.getSerializedName() + "_shard", this, new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));
	}

	@LoadEvent
	public final void setup(ZCommonSetup event) {
		shardTag = ItemTags.create(new ResourceLocation(Quark.MOD_ID, "shards"));
	}
}
