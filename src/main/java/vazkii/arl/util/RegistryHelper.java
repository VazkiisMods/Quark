package vazkii.arl.util;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.google.common.collect.ArrayListMultimap;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;
import vazkii.arl.AutoRegLib;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.arl.interf.IBlockItemProvider;
import vazkii.arl.interf.IItemColorProvider;
import vazkii.arl.interf.IItemPropertiesFiller;

public final class RegistryHelper {

	private static final Map<String, ModData> modData = new HashMap<>();
	
	private static final Queue<Pair<Item, IItemColorProvider>> itemColors = new ArrayDeque<>();
	private static final Queue<Pair<Block, IBlockColorProvider>> blockColors = new ArrayDeque<>();

	private static final Map<Object, ResourceLocation> internalNames = new HashMap<>();
	
	private static ModData getCurrentModData() {
		return getModData(ModLoadingContext.get().getActiveNamespace());
	}

	private static ModData getModData(String modid) {
		ModData data = modData.get(modid);
		if(data == null) {
			data = new ModData();
			modData.put(modid, data);

			FMLJavaModLoadingContext.get().getModEventBus().register(RegistryHelper.class);
		}

		return data;
	}
	
	public static <T> ResourceLocation getRegistryName(T obj, Registry<T> registry) {
		if(internalNames.containsKey(obj))
			return getInternalName(obj);
		
		return registry.getKey(obj);
	}
	
	public static void setInternalName(Object obj, ResourceLocation name) {
		internalNames.put(obj, name);
	}
	
	public static ResourceLocation getInternalName(Object obj) {
		return internalNames.get(obj);
	}
	
	@SubscribeEvent
	public static void onRegistryEvent(RegisterEvent event) {
		getCurrentModData().register(event.getRegistryKey(), event.getForgeRegistry());
	}

	public static void registerBlock(Block block, String resloc) {
		registerBlock(block, resloc, true);
	}

	public static void registerBlock(Block block, String resloc, boolean hasBlockItem) {
		register(block, resloc, Registry.BLOCK_REGISTRY);

		if(hasBlockItem) {
			ModData data = getCurrentModData();
			data.defers.put(Registry.ITEM_REGISTRY.location(), () -> data.createItemBlock(block));
		}

		if(block instanceof IBlockColorProvider)
			blockColors.add(Pair.of(block, (IBlockColorProvider) block));
	}

	public static void registerItem(Item item, String resloc) {
		register(item, resloc, Registry.ITEM_REGISTRY);

		if(item instanceof IItemColorProvider)
			itemColors.add(Pair.of(item, (IItemColorProvider) item));
	}
	
	public static <T> void register(T obj, String resloc, ResourceKey<Registry<T>> registry) {
		if(obj == null)
			throw new IllegalArgumentException("Can't register null object.");

		setInternalName(obj, GameData.checkPrefix(resloc, false));
		getCurrentModData().defers.put(registry.location(), () -> obj);
	}

	public static <T> void register(T obj, ResourceKey<Registry<T>> registry) {
		if(obj == null)
			throw new IllegalArgumentException("Can't register null object.");
		if(getInternalName(obj) == null)
			throw new IllegalArgumentException("Can't register object without registry name.");

		getCurrentModData().defers.put(registry.location(), () -> obj);
	}

	public static void setCreativeTab(Block block, CreativeModeTab group) {
		ResourceLocation res = getInternalName(block);
		if(res == null)
			throw new IllegalArgumentException("Can't set the creative tab for a block without a registry name yet");

		getCurrentModData().groups.put(res, group);
	}

	public static void submitBlockColors(BiConsumer<BlockColor, Block> consumer) {
		blockColors.forEach(p -> consumer.accept(p.getSecond().getBlockColor(), p.getFirst()));
		blockColors.clear();
	}

	public static void submitItemColors(BiConsumer<ItemColor, Item> consumer) {
		itemColors.forEach(p -> consumer.accept(p.getSecond().getItemColor(), p.getFirst()));
		itemColors.clear();
	}

	private static class ModData {

		private Map<ResourceLocation, CreativeModeTab> groups = new LinkedHashMap<>();

		private ArrayListMultimap<ResourceLocation, Supplier<Object>> defers = ArrayListMultimap.create();

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private <T>  void register(ResourceKey<? extends Registry<?>> key, IForgeRegistry<T> registry) {
			ResourceLocation registryRes = key.location();

			if(defers.containsKey(registryRes)) {
				if(registry == null) {
					AutoRegLib.LOGGER.error(registryRes + " does not have a forge registry");
					return;
				}
				
				Collection<Supplier<Object>> ourEntries = defers.get(registryRes);
				for(Supplier<Object> supplier : ourEntries) {
					T entry = (T) supplier.get();
					ResourceLocation name = getInternalName(entry);
					registry.register(name, entry);
					AutoRegLib.LOGGER.debug("Registering to " + registryRes + " - " + name);
				}

				defers.removeAll(registryRes);
			}
		}

		private Item createItemBlock(Block block) {
			Item.Properties props = new Item.Properties();
			ResourceLocation registryName = getInternalName(block);

			CreativeModeTab group = groups.get(registryName);
			if(group != null)
				props = props.tab(group);

			if(block instanceof IItemPropertiesFiller)
				((IItemPropertiesFiller) block).fillItemProperties(props);

			BlockItem blockitem;
			if(block instanceof IBlockItemProvider)
				blockitem = ((IBlockItemProvider) block).provideItemBlock(block, props);
			else blockitem = new BlockItem(block, props);

			if(block instanceof IItemColorProvider)
				itemColors.add(Pair.of(blockitem, (IItemColorProvider) block));

			setInternalName(blockitem, registryName);
			return blockitem;
		}

	}

}
