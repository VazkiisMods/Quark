package vazkii.quark.world.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.arl.block.BlockMod;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.quark.base.handler.BiomeTypeConfigHandler;
import vazkii.quark.base.handler.DimensionConfig;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.building.feature.VanillaWalls;
import vazkii.quark.world.block.BlockBiomeCobblestone;
import vazkii.quark.world.block.BlockGlowcelium;
import vazkii.quark.world.block.BlockGlowshroom;
import vazkii.quark.world.block.slab.BlockFireStoneSlab;
import vazkii.quark.world.block.slab.BlockIcyStoneSlab;
import vazkii.quark.world.block.stairs.BlockFireStoneStairs;
import vazkii.quark.world.block.stairs.BlockIcyStoneStairs;
import vazkii.quark.world.world.UndergroundBiomeGenerator;
import vazkii.quark.world.world.underground.UndergroundBiome;
import vazkii.quark.world.world.underground.UndergroundBiomeGlowshroom;
import vazkii.quark.world.world.underground.UndergroundBiomeIcy;
import vazkii.quark.world.world.underground.UndergroundBiomeLava;
import vazkii.quark.world.world.underground.UndergroundBiomeLush;
import vazkii.quark.world.world.underground.UndergroundBiomeOvergrown;
import vazkii.quark.world.world.underground.UndergroundBiomePrismarine;
import vazkii.quark.world.world.underground.UndergroundBiomeSandstone;
import vazkii.quark.world.world.underground.UndergroundBiomeSlime;
import vazkii.quark.world.world.underground.UndergroundBiomeSpiderNest;

public class UndergroundBiomes extends Feature {

	public static List<UndergroundBiomeGenerator> biomes;
	
	public static BlockMod biome_cobblestone;
	public static BlockMod glowcelium;
	public static Block glowshroom;
	
	public static int glowshroomGrowthRate;
	
	public static IBlockState firestoneState, icystoneState;
	
	public static boolean firestoneEnabled, icystoneEnabled, glowceliumEnabled;
	boolean enableStairsAndSlabs, enableWalls;
	
	@Override
	public void setupConfig() {
		biomes = new ArrayList();
		
		firestoneEnabled = loadPropBool("Enable Firestone", "", true);
		icystoneEnabled = loadPropBool("Enable Froststone", "", true);
		glowceliumEnabled = loadPropBool("Enable Glowcelium and Glowshrooms", "", true);
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true)  && GlobalConfig.enableVariants;
		enableWalls = loadPropBool("Enable walls", "", true)  && GlobalConfig.enableVariants;

		glowshroomGrowthRate = loadPropInt("Glowshroom Growth Rate", "The smaller, the faster glowshrooms will spread. Vanilla mushroom speed is 25.", 20);
		
		biomes.add(loadUndergrondBiomeInfo("Lush", new UndergroundBiomeLush(), 160, Type.JUNGLE));
		biomes.add(loadUndergrondBiomeInfo("Sandstone", new UndergroundBiomeSandstone(), 160, Type.SANDY));
		biomes.add(loadUndergrondBiomeInfo("Slime", new UndergroundBiomeSlime(), 240, Type.SWAMP));
		biomes.add(loadUndergrondBiomeInfo("Prismarine", new UndergroundBiomePrismarine(), 200, Type.OCEAN));
		biomes.add(loadUndergrondBiomeInfo("Spider", new UndergroundBiomeSpiderNest(), 160, Type.PLAINS));
		biomes.add(loadUndergrondBiomeInfo("Overgrown", new UndergroundBiomeOvergrown(), 160, Type.FOREST));
		biomes.add(loadUndergrondBiomeInfo("Icy", new UndergroundBiomeIcy(), 160, Type.COLD));
		biomes.add(loadUndergrondBiomeInfo("Lava", new UndergroundBiomeLava(), 160, Type.MESA));
		biomes.add(loadUndergrondBiomeInfo("Glowshroom", new UndergroundBiomeGlowshroom(), 160, Type.MOUNTAIN, Type.MUSHROOM));
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		if(firestoneEnabled || icystoneEnabled)
			biome_cobblestone = new BlockBiomeCobblestone();
		
		if(enableStairsAndSlabs) {
			if(firestoneEnabled) {
				BlockModSlab.initSlab(biome_cobblestone, 0, new BlockFireStoneSlab(false), new BlockFireStoneSlab(true));
				BlockModStairs.initStairs(biome_cobblestone, 0, new BlockFireStoneStairs());
			}
			
			if(icystoneEnabled) {
				BlockModSlab.initSlab(biome_cobblestone, 1, new BlockIcyStoneSlab(false), new BlockIcyStoneSlab(true));
				BlockModStairs.initStairs(biome_cobblestone, 1, new BlockIcyStoneStairs());
			}
		}

		VanillaWalls.add("fire_stone", biome_cobblestone, 0, enableWalls && firestoneEnabled);
		VanillaWalls.add("icy_stone", biome_cobblestone, 1, enableWalls && icystoneEnabled);

		if(glowceliumEnabled) {
			glowcelium = new BlockGlowcelium();
			glowshroom = new BlockGlowshroom();
			
			RecipeHandler.addShapelessOreDictRecipe(new ItemStack(Items.MUSHROOM_STEW), "mushroomAny", "mushroomAny", new ItemStack(Items.BOWL));
		}
		
		if(firestoneEnabled)
			firestoneState = biome_cobblestone.getDefaultState().withProperty(biome_cobblestone.getVariantProp(), BlockBiomeCobblestone.Variants.FIRE_STONE);
		if(icystoneEnabled)
			icystoneState = biome_cobblestone.getDefaultState().withProperty(biome_cobblestone.getVariantProp(), BlockBiomeCobblestone.Variants.ICY_STONE);
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		if(glowceliumEnabled) {
			OreDictionary.registerOre("mushroomAny", Blocks.RED_MUSHROOM);
			OreDictionary.registerOre("mushroomAny", Blocks.BROWN_MUSHROOM);	
			OreDictionary.registerOre("mushroomAny", glowshroom);
		}
	}
	
	@SubscribeEvent
	public void onOreGenerate(OreGenEvent.GenerateMinable event) {
		if(event.getType() == EventType.DIRT) {
			World world = event.getWorld();
			BlockPos pos = event.getPos();
			
			Chunk chunk = world.getChunkFromBlockCoords(pos);

			for(UndergroundBiomeGenerator gen : biomes)
				gen.generate(chunk.x, chunk.z, world);
		}
	}
	
	@Override
	public boolean hasOreGenSubscriptions() {
		return true;
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
	private UndergroundBiomeGenerator loadUndergrondBiomeInfo(String name, UndergroundBiome biome, int rarity, BiomeDictionary.Type... biomes) {
		String category = configCategory + "." + name;
		UndergroundBiomeInfo info = new UndergroundBiomeInfo(category, biome, rarity, biomes);

		return new UndergroundBiomeGenerator(info);
	}
	
	public static class UndergroundBiomeInfo {
		
		public final boolean enabled;
		public final UndergroundBiome biome;
		public final DimensionConfig dims;
		public final List<BiomeDictionary.Type> types;
		public final int rarity;
		public final int minXSize, minYSize, minZSize;
		public final int xVariation, yVariation, zVariation;
		public final int minY, maxY;
		
		private UndergroundBiomeInfo(String category, UndergroundBiome biome, int rarity, BiomeDictionary.Type... biomes) {
			this.enabled = ModuleLoader.config.getBoolean("Enabled", category, true, "");
			this.biome = biome;
			this.types = BiomeTypeConfigHandler.parseBiomeTypeArrayConfig("Allowed Biome Types", category, biomes);
			this.rarity = ModuleLoader.config.getInt("Rarity", category, rarity, 0, Integer.MAX_VALUE, "This biome will spawn in 1 of X valid chunks");
			
			dims = new DimensionConfig(category);
			
			minY = ModuleLoader.config.getInt("Minimum Y Level", category, 10, 0, 255, "");
			maxY = ModuleLoader.config.getInt("Maximum Y Level", category, 40, 0, 255, "");
			
			minXSize = ModuleLoader.config.getInt("X Minimum", category, 26, 0, Integer.MAX_VALUE, "");
			minYSize = ModuleLoader.config.getInt("Y Minimum", category, 12, 0, Integer.MAX_VALUE, "");
			minZSize = ModuleLoader.config.getInt("Z Minimum", category, 26, 0, Integer.MAX_VALUE, "");
			
			xVariation = ModuleLoader.config.getInt("X Variation", category, 14, 0, Integer.MAX_VALUE, "");
			yVariation = ModuleLoader.config.getInt("Y Variation", category, 6, 0, Integer.MAX_VALUE, "");
			zVariation = ModuleLoader.config.getInt("Z Variation", category, 14, 0, Integer.MAX_VALUE, "");
			
			biome.setupBaseConfig(category);
		}
		
	}
	
}
