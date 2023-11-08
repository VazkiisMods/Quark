package org.violetmoon.zeta.client.event.load;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.world.level.block.Block;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

import java.util.Map;
import java.util.function.Function;

public interface ZAddBlockColorHandlers extends IZetaLoadEvent {
	void register(BlockColor c, Block... blocks);
	void registerNamed(Function<Block, BlockColor> c, String... names);
	BlockColors getBlockColors();

	Post makePostEvent();
	interface Post extends ZAddBlockColorHandlers {
		Map<String, Function<Block, BlockColor>> getNamedBlockColors();
	}
}
