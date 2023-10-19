package vazkii.quark.content.tweaks.module;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;
import vazkii.zeta.event.ZConfigChanged;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "tweaks")
public class MoreBannerLayersModule extends QuarkModule {

	@Config
	@Config.Min(1)
	@Config.Max(16)
	public static int layerLimit = 16;

	@Hint(key = "banner_layer_buff", content = "layerLimit")
	public static final TagKey<Item> banners = ItemTags.BANNERS;

	private static boolean staticEnabled;

	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
		staticEnabled = enabled;
	}

	public static int getLimit(int curr) {
		return staticEnabled ? layerLimit : curr;
	}

}
