package vazkii.quark.oddities;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vazkii.quark.base.module.Module;
import vazkii.quark.oddities.feature.Pipes;

public class QuarkOddities extends Module {

	@Override
	public void addFeatures() {
		registerFeature(new Pipes());
	}
	
	@Override
	public ItemStack getIconStack() {
		return new ItemStack(Items.CHORUS_FRUIT);
	}
	
}
