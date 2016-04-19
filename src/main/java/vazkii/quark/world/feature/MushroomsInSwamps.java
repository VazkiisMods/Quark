/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [24/03/2016, 16:04:11 (GMT)]
 */
package vazkii.quark.world.feature;

import net.minecraft.init.Biomes;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import vazkii.quark.base.module.Feature;

public class MushroomsInSwamps extends Feature {

	int bigMushroomsPerChunk;

	@Override
	public void setupConfig() {
		bigMushroomsPerChunk = loadPropInt("Big mushrooms per chunk", "", 1);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		Biomes.swampland.theBiomeDecorator.bigMushroomsPerChunk = bigMushroomsPerChunk;
	}

}
