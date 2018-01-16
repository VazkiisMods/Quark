/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [01/06/2016, 20:37:07 (GMT)]
 */
package vazkii.quark.world.feature;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class NaturalBlazesInNether extends Feature {

	int weight, min, max;
	boolean restrictToNetherrack;
	
	List<String> allowedBlocks;

	@Override
	public void setupConfig() {
		weight = loadPropInt("Spawn Weight", "The higher, the more will spawn", 5);
		min = loadPropInt("Smallest spawn group", "", 1);
		max = loadPropInt("Largest spawn group", "", 2);
		
		restrictToNetherrack = loadPropBool("Block restrictions", "Make naturally spawned blazes only spawn in allowed blocks", true);
		allowedBlocks = Arrays.asList(loadPropStringList("Allowed spawn blocks", "Only used if \" Block restrictions\" is enabled.", new String[] {
				Blocks.NETHERRACK.getRegistryName().toString(),
				Blocks.SOUL_SAND.getRegistryName().toString(),
				Blocks.MAGMA.getRegistryName().toString(),
				"quark:basalt"
		}));
	}

	@Override
	public void init(FMLInitializationEvent event) {
		SpawnListEntry blazeEntry = new SpawnListEntry(EntityBlaze.class, weight, min, max);
		BiomeDictionary.getBiomes(BiomeDictionary.Type.NETHER).forEach(biome -> biome.getSpawnableList(EnumCreatureType.MONSTER).add(blazeEntry));
	}
	
	@SubscribeEvent
	public void onSpawn(LivingSpawnEvent.CheckSpawn event) {
		if(restrictToNetherrack && !event.isSpawner() && event.getEntityLiving() instanceof EntityBlaze && event.getResult() != Result.DENY && event.getEntityLiving().world instanceof WorldServer) {
			EntityBlaze blaze = (EntityBlaze) event.getEntityLiving();
			WorldServer world = (WorldServer) blaze.world;
			BlockPos pos = blaze.getPosition();
			boolean allowedBlock = allowedBlocks.contains(world.getBlockState(pos.down()).getBlock().getRegistryName().toString());
			boolean fortress = world.getChunkProvider().isInsideStructure(world, "Fortress", pos);
			if(!fortress && !allowedBlock)
				event.setResult(Result.DENY);
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
	@Override
	public String getFeatureIngameConfigName() {
		return "Naturally Spawning Blazes";
	}
	
}
