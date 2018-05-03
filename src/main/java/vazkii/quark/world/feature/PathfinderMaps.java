package vazkii.quark.world.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Biomes;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration.Type;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.experimental.world.BiomeLocator;

public class PathfinderMaps extends Feature {

	public static Multimap<Integer, TradeInfo> trades;

	public static boolean unlockAllAtOnce;
	public static boolean multipleAtFirstUnlock;
	public static boolean printAllBiomeNames;
	
	private String[] customs;
	
	@Override
	public void setupConfig() {
		trades = HashMultimap.create();
		
		unlockAllAtOnce = loadPropBool("Unlock All Trades at Once", "By default, when a Cartographer levels up, a random Pathfinder Map from that level is added to their trades."
				+ "\nSet this to true to add all the maps from that level to the trades instead.", false);
		multipleAtFirstUnlock = loadPropBool("Unlock Multiples At Level 2", "By default, when a Cartographer evolves to level 2, two or three Pathfinder Maps are unlocked."
				+ "\nSet this to false to disable this, and make it only unlock one, like in the other levels.", true);
		
		loadTradeInfo(Biomes.ICE_PLAINS, true, 2, 8, 14, 0x7FE4FF, "ice_plains");
		loadTradeInfo(Biomes.EXTREME_HILLS, true, 2, 8, 14, 0x8A8A8A);
		loadTradeInfo(Biomes.ROOFED_FOREST, true, 2, 8, 14, 0x00590A);
		loadTradeInfo(Biomes.DESERT, true, 2, 8, 14, 0xCCB94E);
		loadTradeInfo(Biomes.SAVANNA, true, 2, 8, 14, 0x9BA562);

		loadTradeInfo(Biomes.SWAMPLAND, true, 3, 12, 18, 0x22370F);
		loadTradeInfo(Biomes.REDWOOD_TAIGA, true, 3, 12, 18, 0x5B421F);
		loadTradeInfo(Biomes.MUTATED_FOREST, true, 3, 12, 18, 0xDC7BEA, "flower_forest");
		
		loadTradeInfo(Biomes.JUNGLE, true, 4, 16, 22, 0x22B600);
		loadTradeInfo(Biomes.MESA, true, 4, 16, 22, 0xC67F22);

		loadTradeInfo(Biomes.MUSHROOM_ISLAND, true, 5, 20, 26, 0x4D4273);
		loadTradeInfo(Biomes.MUTATED_ICE_FLATS, true, 5, 20, 26, 0x41D6C9, "ice_spikes");
		
		String desc = "In this section you can add custom Pathfinder Maps. This works for both vanilla and modded biomes.\n"
				+ "Each custom map must be on its own line.\n"
				+ "The format for a custom map is as follows:\n"
				+ "<id>,<level>,<min_price>,<max_price>,<color>,<name>\n\n"
				+ "With the following descriptions:\n"
				+ " - <id> being the biome's ID NAME. You can find vanilla names here - https://minecraft.gamepedia.com/Biome#Biome_IDs\n"
				+ " - <level> being the Cartographer villager level required for the map to be unlockable\n"
				+ " - <min_price> being the cheapest (in Emeralds) the map can be\n"
				+ " - <max_price> being the most expensive (in Emeralds) the map can be\n"
				+ " - <color> being a hex color (without the #) for the map to display. You can generate one here - http://htmlcolorcodes.com/\n"
				+ " - <name> being the display name of the map\n\n"
				+ "Here's an example of a map to locate Ice Mountains:\n"
				+ "minecraft:ice_mountains,2,8,14,7FE4FF,Ice Mountains Pathfinder Map";
		customs = loadPropStringList("Custom Map Info", desc, new String[0]);
 	}
	
	@SubscribeEvent
	public void onRegisterVillagers(RegistryEvent.Register<VillagerProfession> event) {
		loadCustomMaps(customs);
		
		VillagerProfession librarian = event.getRegistry().getValue(new ResourceLocation("minecraft:librarian"));
		VillagerCareer cartographer = librarian.getCareer(1);
		
		for(Integer level : trades.keySet())
			cartographer.addTrade(level, new PathfinderMapTrade(level));
 	}
	
	private void loadTradeInfo(Biome biome, boolean enabled, int level, int minPrice, int maxPrice, int color) {
		loadTradeInfo(biome, enabled, level, minPrice, maxPrice, color, "", "");
	}
	
	private void loadTradeInfo(Biome biome, boolean enabled, int level, int minPrice, int maxPrice, int color, String overrideCategory) {
		loadTradeInfo(biome, enabled, level, minPrice, maxPrice, color, overrideCategory, "");
	}
	
	private void loadTradeInfo(Biome biome, boolean enabled, int level, int minPrice, int maxPrice, int color, String overrideCategory, String overrideName) {
		String category = configCategory + ".";
		if(!overrideCategory.isEmpty())
			category += overrideCategory;
		else category += biome.getRegistryName().getResourcePath();
		
		TradeInfo info;
		if(overrideName.isEmpty())
			info = new TradeInfo(category, biome, enabled, level, minPrice, maxPrice, color);
		else info = new TradeInfo(category, biome, enabled, level, minPrice, maxPrice, color, overrideName);
		
		if(info.enabled)
			trades.put(info.level, info);
	}
	
	private void loadTradeInfo(String line) throws IllegalArgumentException {
		String[] tokens = line.split(",");
		if(tokens.length != 6)
			throw new IllegalArgumentException("Wrong number of parameters " + tokens.length + " (expected 6)");
		
		ResourceLocation biomeName = new ResourceLocation(tokens[0]);
		if(!Biome.REGISTRY.containsKey(biomeName))
			throw new IllegalArgumentException("No biome exists with name " + biomeName);
		
		Biome biome = Biome.REGISTRY.getObject(biomeName);
		int level = Integer.parseInt(tokens[1]);
		int minPrice = Integer.parseInt(tokens[2]);
		int maxPrice = Integer.parseInt(tokens[3]);
		int color = Integer.parseInt(tokens[4], 16);
		String name = tokens[5];
		
		loadTradeInfo(biome, true, level, minPrice, maxPrice, color, "", name);
	}
	
	private void loadCustomMaps(String[] lines) {
		for(String s : lines)
			try {
				loadTradeInfo(s);
			} catch(IllegalArgumentException e) {
				LibMisc.LOGGER.warn("[Custom Pathfinder Maps] Error while reading custom map string \"{}\"", s);
				LibMisc.LOGGER.warn("[Custom Pathfinder Maps] - {}", e.getMessage());
			}
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
	public static ItemStack createMap(World world, BlockPos pos, TradeInfo info) {
		BlockPos biomePos = BiomeLocator.spiralOutwardsLookingForBiome(world, info.biome, pos.getX(), pos.getZ());

		if(biomePos == null)
			return ItemStack.EMPTY;
		
		int id = world.getUniqueDataId("map");
		ItemStack stack = new ItemStack(Items.FILLED_MAP, 1, id);
		stack.setTranslatableName(info.name);
		NBTTagCompound cmp = ItemNBTHelper.getCompound(stack, "display", false);
		cmp.setInteger("MapColor", info.color);
		ItemNBTHelper.setCompound(stack, "display", cmp);

		String s = "map_" + id;
		MapData mapdata = new MapData(s);
		world.setData(s, mapdata);
		mapdata.scale = 2;
		mapdata.xCenter = biomePos.getX() + (int) ((Math.random() - 0.5) * 200);
		mapdata.zCenter = biomePos.getZ() + (int) ((Math.random() - 0.5) * 200);
		mapdata.dimension = 0;
		mapdata.trackingPosition = true;
		mapdata.unlimitedTracking = true;

		ItemMap.renderBiomePreviewMap(world, stack);
		MapData.addTargetDecoration(stack, biomePos, "+", Type.TARGET_X);

		return stack;
	}

	private static class PathfinderMapTrade implements EntityVillager.ITradeList {
		
		public final int level;
		
		public PathfinderMapTrade(int level) {
			this.level = level;
		}

		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
			List<TradeInfo> infos = new ArrayList(trades.get(level));
			if(infos == null || infos.isEmpty())
				return;
			
			if(unlockAllAtOnce)
				for(TradeInfo info : infos)
					unlock(merchant, recipeList, random, info);
			else {
				int amount = (level == 2 && multipleAtFirstUnlock) ? Math.min(infos.size(), 2 + random.nextInt(2)) : 1;
				
				for(int i = 0; i < amount; i++) {
					TradeInfo info = infos.get(random.nextInt(infos.size()));
					unlock(merchant, recipeList, random, info);
					infos.remove(info);
				}
			}
		}
		
		private void unlock(IMerchant merchant, MerchantRecipeList recipeList, Random random, TradeInfo info) {
			int i = random.nextInt(info.maxPrice - info.minPrice + 1) + info.minPrice;
			World world = merchant.getWorld();

			ItemStack itemstack = createMap(merchant.getWorld(), merchant.getPos(), info); 
			if(itemstack.isEmpty())
				return;
			
			MerchantRecipe recipe = new MerchantRecipe(new ItemStack(Items.EMERALD, i), new ItemStack(Items.COMPASS), itemstack, 0, 1);
			recipeList.add(recipe);
		}
	}

	public static class TradeInfo {
		
		public final boolean enabled;
		public final Biome biome;
		public final int level;
		public final int minPrice;
		public final int maxPrice;
		public final int color;
		public final String name;
		
		TradeInfo(String category, Biome biome, boolean enabled, int level, int minPrice, int maxPrice, int color) {
			this(category, biome, enabled, level, minPrice, maxPrice, color, "quark.biomeMap." + biome.getRegistryName().getResourcePath());
		}
		
		TradeInfo(String category, Biome biome, boolean enabled, int level, int minPrice, int maxPrice, int color, String name) {
			this.enabled = ModuleLoader.config.getBoolean("Enabled", category, enabled, "");
			this.biome = biome;
			this.level = ModuleLoader.config.getInt("Required Villager Level", category, level, 0, 10, "");
			this.minPrice = ModuleLoader.config.getInt("Minimum Emerald Price", category, minPrice, 1, 64, "");
			this.maxPrice = Math.max(minPrice, ModuleLoader.config.getInt("Maximum Emerald Price", category, maxPrice, 1, 64, ""));
			this.color = color;
			this.name = name;
		}
	}
	
}
