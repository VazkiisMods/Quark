package vazkii.quark.content.tools.item;

import javax.annotation.Nonnull;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import vazkii.quark.api.IRuneColorProvider;
import vazkii.quark.base.item.QuarkItem;
import vazkii.zeta.module.ZetaModule;

/**
 * @author WireSegal
 * Created at 2:27 PM on 8/17/19.
 */
public class RuneItem extends QuarkItem implements IRuneColorProvider {

	private final int color;
	private final boolean glow;

	public RuneItem(String regname, ZetaModule module, int color, boolean glow) {
		super(regname, module, new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS));
		this.color = color;
		this.glow = glow;
	}

	@Override
	public boolean isFoil(@Nonnull ItemStack stack) {
		return glow;
	}

	@Override
	public int getRuneColor(ItemStack stack) {
		return color;
	}
}
