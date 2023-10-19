package vazkii.quark.content.client.module;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.hint.Hint;
import vazkii.zeta.event.ZConfigChanged;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "client")
public class WoolShutsUpMinecartsModule extends QuarkModule {

	private static boolean staticEnabled;
	
	@Hint(key = "wool_muffling") TagKey<Item> dampeners = ItemTags.DAMPENS_VIBRATIONS;
	
	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
		staticEnabled = enabled;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static boolean canPlay(AbstractMinecart cart) {
		return !staticEnabled || !cart.level.getBlockState(cart.blockPosition().below()).is(BlockTags.DAMPENS_VIBRATIONS);
	}
	
}
