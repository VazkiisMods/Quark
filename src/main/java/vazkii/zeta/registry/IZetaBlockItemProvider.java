package vazkii.zeta.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public interface IZetaBlockItemProvider {

	BlockItem provideItemBlock(Block block, Item.Properties props);
	
}
