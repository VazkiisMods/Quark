package vazkii.zeta.block.ext;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockExtensionFactory {

	BlockExtensionFactory DEFAULT = block -> IZetaBlockExtensions.DEFAULT;

	/**
	 * Return an instance of IZetaBlockExtensions for the given block, which is guaranteed to *not* already implement IZetaBlockExtensions.
	 * <br>
	 * Do not call externally.
	 */
	IZetaBlockExtensions getInternal(Block block);

	/**
	 * Returns an instance of IZetaBlockExtensions for the given block. If the block already implements IZetaBlockExtensions, it will be returned.
	 */
	default IZetaBlockExtensions get(Block block) {
		if(block instanceof IZetaBlockExtensions ext)
			return ext;
		else
			return getInternal(block);
	}

	/**
	 * Convenience; equivalent to get(state.getBlock()).
	 */
	default IZetaBlockExtensions get(BlockState state) {
		return get(state.getBlock());
	}

}
