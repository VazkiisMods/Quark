package vazkii.quark.world.feature;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.arl.block.BlockMod;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.handler.BiomeTypeConfigHandler;
import vazkii.quark.base.handler.DimensionConfig;
import vazkii.quark.base.handler.ModIntegrationHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.building.feature.VanillaWalls;
import vazkii.quark.world.block.BlockLimestone;
import vazkii.quark.world.block.BlockMarble;
import vazkii.quark.world.block.slab.BlockLimestoneSlab;
import vazkii.quark.world.block.slab.BlockMarbleSlab;
import vazkii.quark.world.block.stairs.BlockLimestoneStairs;
import vazkii.quark.world.block.stairs.BlockMarbleStairs;
import vazkii.quark.world.world.StoneInfoBasedGenerator;

public class RevampStoneGen extends Feature {

	public static BlockMod marble;
	public static BlockMod limestone;

	boolean enableStairsAndSlabs;
	boolean enableWalls;
	boolean outputCSV;
	
	public static boolean generateBasedOnBiomes;
	public static boolean enableMarble;
	public static boolean enableLimestone;

	public static StoneInfo graniteInfo, dioriteInfo, andesiteInfo, marbleInfo, limestoneInfo;
	private static List<StoneInfoBasedGenerator> generators;

	@Override
	public void setupConfig() {
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
		enableWalls = loadPropBool("Enable walls", "", true) && GlobalConfig.enableVariants;
		enableMarble = loadPropBool("Enable Marble", "", true);
		enableLimestone = loadPropBool("Enable Limestone", "", true);
		generateBasedOnBiomes = loadPropBool("Generate Based on Biomes", "Note: The stone rarity values are tuned based on this being true. If you turn it off, also change the stones' rarity (around 50 is fine).", true);
		outputCSV = loadPropBool("Output CSV Debug Info", "If this is true, CSV debug info will be printed out to the console on init, to help test biome spreads.", false);

		int defSize = 14;
		int defRarity = 9;
		int defUpper = 80;
		int defLower = 20;

		graniteInfo = loadStoneInfo("granite", defSize, defRarity, defUpper, defLower, true, Type.MOUNTAIN, Type.HILLS);
		dioriteInfo = loadStoneInfo("diorite", defSize, defRarity, defUpper, defLower, true, Type.SANDY, Type.SAVANNA, Type.WASTELAND, Type.MUSHROOM);
		andesiteInfo = loadStoneInfo("andesite", defSize, defRarity, defUpper, defLower, true, Type.FOREST);
		marbleInfo = loadStoneInfo("marble", defSize, defRarity, defUpper, defLower, enableMarble, Type.PLAINS, Type.SNOWY);
		limestoneInfo = loadStoneInfo("limestone", defSize, defRarity, defUpper, defLower, enableLimestone, Type.SWAMP, Type.OCEAN, Type.RIVER, Type.BEACH, Type.JUNGLE);
	}
	
	public StoneInfo loadStoneInfo(String name, int clusterSize, int clusterRarity, int upperBound, int lowerBound, boolean enabled, BiomeDictionary.Type... biomes) {
		return loadStoneInfo(configCategory, name, clusterSize, clusterRarity, upperBound, lowerBound, enabled, "0", biomes);
	}

	public static StoneInfo loadStoneInfo(String configCategory, String name, int clusterSize, int clusterRarity, int upperBound, int lowerBound, boolean enabled, String dims, BiomeDictionary.Type... biomes) {
		String category = configCategory + "." + name;
		StoneInfo info = new StoneInfo(category, clusterSize, clusterRarity, upperBound, lowerBound, enabled, dims, biomes);

		return info;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		if(enableMarble) {
			marble = new BlockMarble();

			if(enableStairsAndSlabs) {
				BlockModSlab.initSlab(marble, 0, new BlockMarbleSlab(false), new BlockMarbleSlab(true));
				BlockModStairs.initStairs(marble, 0, new BlockMarbleStairs());
			}

			VanillaWalls.add("marble", marble, 0, enableWalls);

			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(marble, 4, 1),
					"BB", "BB",
					'B', ProxyRegistry.newStack(marble, 1, 0));
		}

		if(enableLimestone) {
			limestone = new BlockLimestone();

			if(enableStairsAndSlabs) {
				BlockModSlab.initSlab(limestone, 0, new BlockLimestoneSlab(false), new BlockLimestoneSlab(true));
				BlockModStairs.initStairs(limestone, 0, new BlockLimestoneStairs());
			}

			VanillaWalls.add("limestone", limestone, 0, enableWalls);

			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(limestone, 4, 1),
					"BB", "BB",
					'B', ProxyRegistry.newStack(limestone, 1, 0));
		}

		IBlockState graniteState = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE);
		IBlockState dioriteState = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE);
		IBlockState andesiteState = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE);

		generators = new ArrayList();
		
		generators.add(new StoneInfoBasedGenerator(() -> graniteInfo, graniteState, "granite"));
		generators.add(new StoneInfoBasedGenerator(() -> dioriteInfo, dioriteState, "diorite"));
		generators.add(new StoneInfoBasedGenerator(() -> andesiteInfo, andesiteState, "andesite"));

		if(enableMarble)
			generators.add(new StoneInfoBasedGenerator(() -> marbleInfo, marble.getDefaultState(), "marble"));
		if(enableLimestone)
			generators.add(new StoneInfoBasedGenerator(() -> limestoneInfo, limestone.getDefaultState(), "limestone"));
		
		if(outputCSV)
			BiomeTypeConfigHandler.debugStoneGeneration(generators);
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		if(enableMarble) {
			OreDictionary.registerOre("stoneMarble", ProxyRegistry.newStack(marble, 1, 0));
			OreDictionary.registerOre("stoneMarblePolished", ProxyRegistry.newStack(marble, 1, 1));
			ModIntegrationHandler.registerChiselVariant("marble", ProxyRegistry.newStack(marble, 1, 0));
			ModIntegrationHandler.registerChiselVariant("marble", ProxyRegistry.newStack(marble, 1, 1));
		}
		
		if(enableLimestone) {
			OreDictionary.registerOre("stoneLimestone", ProxyRegistry.newStack(limestone, 1, 0));
			OreDictionary.registerOre("stoneLimestonePolished", ProxyRegistry.newStack(limestone, 1, 1));
			ModIntegrationHandler.registerChiselVariant("limestone", ProxyRegistry.newStack(limestone, 1, 0));
			ModIntegrationHandler.registerChiselVariant("limestone", ProxyRegistry.newStack(limestone, 1, 1));
		}
	}
	
	@SubscribeEvent
	public void onOreGenerate(OreGenEvent.GenerateMinable event) {
		switch(event.getType()) {
		case GRANITE:
			if(graniteInfo.enabled)
				event.setResult(Result.DENY);
			break;
		case DIORITE:
			if(dioriteInfo.enabled)
				event.setResult(Result.DENY);
			break;
		case ANDESITE:
			if(andesiteInfo.enabled)
				event.setResult(Result.DENY);
			
			generateNewStones(event);
			break;
		default: return;
		}
	}

	private void generateNewStones(OreGenEvent.GenerateMinable event) {
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		Chunk chunk = world.getChunkFromBlockCoords(pos);
		
		for(StoneInfoBasedGenerator gen : generators)
			gen.generate(chunk.x, chunk.z, world);
	}
	
	@Override
	public boolean hasOreGenSubscriptions() {
		return true;
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

	public static class StoneInfo {

		public final boolean enabled;
		public final int clusterSize, clusterRarity, upperBound, lowerBound;
		public final boolean clustersRarityPerChunk;
		
		public final DimensionConfig dims;
		public final List<BiomeDictionary.Type> allowedBiomes;

		private StoneInfo(String category, int clusterSize, int clusterRarity, int upperBound, int lowerBound, boolean enabled, String dimStr, BiomeDictionary.Type... biomes) {
			this.enabled = ModuleLoader.config.getBoolean("Enabled", category, true, "") && enabled;
			this.clusterSize = ModuleLoader.config.getInt("Cluster Radius", category, clusterSize, 0, Integer.MAX_VALUE, "");
			this.clusterRarity = ModuleLoader.config.getInt("Cluster Rarity", category, clusterRarity, 0, Integer.MAX_VALUE, "Out of how many chunks would one of these clusters generate");
			this.upperBound = ModuleLoader.config.getInt("Y Level Max", category, upperBound, 0, 255, "");
			this.lowerBound = ModuleLoader.config.getInt("Y Level Min", category, lowerBound, 0, 255, "");
			clustersRarityPerChunk = ModuleLoader.config.getBoolean("Invert Cluster Rarity", category, false, "Setting this to true will make the 'Cluster Rarity' feature be X per chunk rather than 1 per X chunks");
			
			dims = new DimensionConfig(category, dimStr);
			allowedBiomes = BiomeTypeConfigHandler.parseBiomeTypeArrayConfig("Allowed Biome Types", category, biomes);
		}
	}

}

