package vazkii.arl.item;

import net.minecraft.world.item.Item;
import vazkii.arl.util.RegistryHelper;

import net.minecraft.world.item.Item.Properties;

public class BasicItem extends Item {

	public BasicItem(String regname, Properties properties) {
		super(properties);
		
		RegistryHelper.registerItem(this, regname);
	}

}
