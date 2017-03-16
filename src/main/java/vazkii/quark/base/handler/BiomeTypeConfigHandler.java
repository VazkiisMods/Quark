package vazkii.quark.base.handler;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.world.world.StoneInfoBasedGenerator;

public final class BiomeTypeConfigHandler {

	public static List<BiomeDictionary.Type> parseBiomeTypeArrayConfig(String name, String category, BiomeDictionary.Type... biomes) {
		String[] defaultBiomes = Arrays.stream(biomes).<String>map(b -> b.getName()).toArray(i -> new String[i]);
		String[] readBiomes = ModuleLoader.config.getStringList(name, category, defaultBiomes, 
				"Biome Type List: https://github.com/MinecraftForge/MinecraftForge/blob/1.11.x/src/main/java/net/minecraftforge/common/BiomeDictionary.java#L44-L90\n"
				+ "Types per Biome: https://github.com/MinecraftForge/MinecraftForge/blob/1.11.x/src/main/java/net/minecraftforge/common/BiomeDictionary.java#L402-L463");
		
		return Arrays.stream(readBiomes).map(s -> BiomeDictionary.Type.getType(s)).collect(Collectors.toList());
	}
	
	public static boolean biomeTypeIntersectCheck(Iterable<BiomeDictionary.Type> biomeItr, Biome b) {
		Set<BiomeDictionary.Type> currentTypes = BiomeDictionary.getTypes(b);

			for(BiomeDictionary.Type type : biomeItr)
				if(currentTypes.contains(type))
					return true;

			return false;
	}
	
	public static void debugStoneGeneration(Iterable<StoneInfoBasedGenerator> generators) {
		System.out.println("### OUTPUTTING BIOME CSV DATA ###");
		System.out.print("sep=;\nBiome");
		for(StoneInfoBasedGenerator gen : generators)
			System.out.print(";" + WordUtils.capitalize(gen.name));
		System.out.print(";Biome Type");
		System.out.println();
		for(ResourceLocation r : Biome.REGISTRY.getKeys()) {
			Biome b = Biome.REGISTRY.getObject(r);
			System.out.print(b.getBiomeName());
			for(StoneInfoBasedGenerator gen : generators) {
				if(gen.canGenerateInBiome(b))
					System.out.print(";yes");
				else System.out.print(";no");
			}
			System.out.print(";" + (b.isMutation() ? "mutation" : "normal"));
			System.out.println();
		}
		System.out.println("### DONE ###");
	}
	
}
