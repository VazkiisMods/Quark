package vazkii.arl.util;

import java.util.function.BiConsumer;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import vazkii.quark.base.Quark;

//TODO ZETA: simply inline these calls
@Deprecated(since = "zeta")
public final class RegistryHelper {
	public static <T> ResourceLocation getRegistryName(T obj, Registry<T> registry) {
		return Quark.REGISTRY.getRegistryName(obj, registry);
	}
	
	public static void setInternalName(Object obj, ResourceLocation name) {
		Quark.REGISTRY.setInternalName(obj, name);
	}
	
	public static ResourceLocation getInternalName(Object obj) {
		return Quark.REGISTRY.getInternalName(obj);
	}

	public static void registerBlock(Block block, String resloc) {
		Quark.REGISTRY.registerBlock(block, resloc, true);
	}

	public static void registerBlock(Block block, String resloc, boolean hasBlockItem) {
		Quark.REGISTRY.registerBlock(block, resloc, hasBlockItem);
	}

	public static void registerItem(Item item, String resloc) {
		Quark.REGISTRY.registerItem(item, resloc);
	}
	
	public static <T> void register(T obj, String resloc, ResourceKey<Registry<T>> registry) {
		Quark.REGISTRY.register(obj, resloc, registry);
	}

	public static <T> void register(T obj, ResourceKey<Registry<T>> registry) {
		Quark.REGISTRY.register(obj, registry);
	}

	public static void setCreativeTab(Block block, CreativeModeTab group) {
		Quark.REGISTRY.setCreativeTab(block, group);
	}

	public static void submitBlockColors(BiConsumer<BlockColor, Block> consumer) {
		Quark.REGISTRY.submitBlockColors(consumer);
	}

	public static void submitItemColors(BiConsumer<ItemColor, Item> consumer) {
		Quark.REGISTRY.submitItemColors(consumer);
	}
}
