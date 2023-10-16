package vazkii.arl.interf;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public interface IBlockItemProvider {

	BlockItem provideItemBlock(Block block, Item.Properties props);
	
}
