/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [20/03/2016, 16:03:14 (GMT)]
 */
package vazkii.quark.world.feature;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.world.ClayGenerator;

public class ClayGeneration extends Feature {

	int clusterSize, clusterCount;

	@Override
	public void setupConfig() {
		clusterSize = loadPropInt("Cluster size", "", 33);
		clusterCount = loadPropInt("Cluster count", "", 4);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		GameRegistry.registerWorldGenerator(new ClayGenerator(clusterSize, clusterCount), 0);
	}

}
