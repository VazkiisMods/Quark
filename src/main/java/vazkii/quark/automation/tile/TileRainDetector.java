/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 * 
 * File Created @ [18/04/2016, 19:55:34 (GMT)]
 */
package vazkii.quark.automation.tile;

import net.minecraft.block.BlockDaylightDetector;
import net.minecraft.util.ITickable;
import vazkii.quark.automation.block.BlockRainDetector;
import vazkii.quark.base.block.tile.TileMod;

public class TileRainDetector extends TileMod implements ITickable {

	@Override
    public void update() {
        if(worldObj != null && !worldObj.isRemote && worldObj.getTotalWorldTime() % 20L == 0L) {
            blockType = getBlockType();

            if(blockType instanceof BlockRainDetector)
                ((BlockRainDetector) blockType).updatePower(worldObj, pos);
        }
    }
	
}
