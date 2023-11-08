package org.violetmoon.zeta.registry;

import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * note this is NOT client-side code, for ease of registering stuff in constructors
 *
 * @see org.violetmoon.zeta.client.ClientRegistryExtension
 */
public class RenderLayerRegistry {

	protected Map<Block, Layer> mapping = new HashMap<>();
	protected Map<Block, Block> inheritances = new HashMap<>();

	public enum Layer {
		SOLID,
		CUTOUT,
		CUTOUT_MIPPED,
		TRANSLUCENT,
	}

	public void put(Block block, Layer layer) {
		if(mapping == null)
			throw new IllegalStateException("Already finalized RenderLayerRegistry");

		mapping.put(block, layer);
	}

	public void mock(Block block, Block inheritFrom) {
		if(inheritances == null)
			throw new IllegalStateException("Already finalized RenderLayerRegistry");

		inheritances.put(block, inheritFrom);
	}

	public void finalize(BiConsumer<Block, Layer> action) {
		//Note: only handles one layer of inheritances
		for(Block b : inheritances.keySet()) {
			Block inheritFrom = inheritances.get(b);
			Layer layer = mapping.get(inheritFrom);

			if(layer != null)
				mapping.put(b, layer);
		}

		mapping.forEach(action);

		//and we're done
		mapping = null;
		inheritances = null;
	}

}
