/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [24/03/2016, 04:47:31 (GMT)]
 */
package vazkii.quark.world.feature;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import vazkii.quark.base.module.Feature;

public class OceanGuardians extends Feature {

	boolean deepOceanOnly;
	int weight, min, max;

	@Override
	public void setupConfig() {
		deepOceanOnly = loadPropBool("Deep ocean only", "", false);
		weight = loadPropInt("Spawn Weight", "(Squids have 10, note that guardians have a 95% chance to not spawn when they would be spawned)", 15);
		min = loadPropInt("Smallest spawn group", "", 2);
		max = loadPropInt("Largest spawn group", "", 4);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		Set<BiomeGenBase> set = deepOceanOnly ? ImmutableSet.of(Biomes.deepOcean) : ImmutableSet.of(Biomes.ocean, Biomes.deepOcean);

		for(BiomeGenBase b : set)
			b.getSpawnableList(EnumCreatureType.WATER_CREATURE).add(new SpawnListEntry(EntityGuardian.class, weight, min, max));
	}

}
