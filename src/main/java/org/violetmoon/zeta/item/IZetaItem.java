package org.violetmoon.zeta.item;

import net.minecraft.world.item.Item;
import org.violetmoon.zeta.module.IDisableable;

public interface IZetaItem extends IDisableable<IZetaItem> {

	default Item getItem() {
		return (Item) this;
	}

}
