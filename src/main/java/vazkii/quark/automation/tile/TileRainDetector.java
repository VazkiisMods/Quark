/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/04/2016, 19:55:34 (GMT)]
 */
package vazkii.quark.automation.tile;

import net.minecraft.util.ITickable;
import vazkii.arl.block.tile.TileMod;
import vazkii.quark.automation.block.BlockRainDetector;

public class TileRainDetector extends TileMod implements ITickable {

	@Override
	public void update() {
		if(getWorld() != null && !getWorld().isRemote && getWorld().getTotalWorldTime() % 20L == 0L) {
			blockType = getBlockType();

			if(blockType instanceof BlockRainDetector)
				((BlockRainDetector) blockType).updatePower(getWorld(), pos);
		}
	}

}
