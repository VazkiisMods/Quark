package vazkii.zeta.registry;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import vazkii.zeta.Zeta;

//Mash of arl's RegistryHelper and its ModData innerclass.
//You're expected to create one of these per modid instead, avoiding a dependency on Forge's "current mod id" notion.
//
//TODO: Tidy up this API a bit - it might be nice to use a "proper" deferredregister on Forge
public abstract class ZetaRegistry {
	protected final Zeta z;
	protected final String modid;

	//the keys of this are things like "minecraft:block", "minecraft:item" and so on
	private final Multimap<ResourceLocation, Supplier<Object>> defers = ArrayListMultimap.create();
	private final Map<Object, ResourceLocation> internalNames = new IdentityHashMap<>();
	private final Map<Item, IZetaItemColorProvider> itemColors = new IdentityHashMap<>();
	private final Map<Block, IZetaBlockColorProvider> blockColors = new IdentityHashMap<>();
	private final Map<ResourceLocation, CreativeModeTab> groups = new LinkedHashMap<>();

	public ZetaRegistry(Zeta z, String modid) {
		this.z = z;
		this.modid = modid;
	}

	public <T> ResourceLocation getRegistryName(T obj, Registry<T> registry) {
		if(internalNames.containsKey(obj))
			return getInternalName(obj);

		return registry.getKey(obj);
	}

	public void setInternalName(Object obj, ResourceLocation name) {
		internalNames.put(obj, name);
	}

	public ResourceLocation getInternalName(Object obj) {
		return internalNames.get(obj);
	}

	public <T> void register(T obj, String resloc, ResourceKey<Registry<T>> registry) {
		if(obj == null)
			throw new IllegalArgumentException("Can't register null object.");

		setInternalName(obj, newResourceLocation(resloc));
		defers.put(registry.location(), () -> obj);
	}

	//You know how `new ResourceLocation(String)` prepends "minecraft" if there's no prefix?
	//This method is like that, except it prepends *your* modid
	public ResourceLocation newResourceLocation(String in) {
		if(in.indexOf(':') == -1) return new ResourceLocation(modid, in);
		else return new ResourceLocation(in);
	}

	//TODO ZETA: what's up with this?
	public <T> void register(T obj, ResourceKey<Registry<T>> registry) {
		if(obj == null)
			throw new IllegalArgumentException("Can't register null object.");
		if(getInternalName(obj) == null)
			throw new IllegalArgumentException("Can't register object without registry name.");

		defers.put(registry.location(), () -> obj);
	}

	public void registerItem(Item item, String resloc) {
		register(item, resloc, Registry.ITEM_REGISTRY);

		if(item instanceof IZetaItemColorProvider)
			itemColors.put(item, (IZetaItemColorProvider) item);
	}

	public void registerBlock(Block block, String resloc) {
		registerBlock(block, resloc, true);
	}

	public void registerBlock(Block block, String resloc, boolean hasBlockItem) {
		register(block, resloc, Registry.BLOCK_REGISTRY);

		//TODO: this supplier is mostly a load-bearing way to defer calling groups.get(registryName),
		// until after CreativeTabHandler.finalizeTabs is called
		if(hasBlockItem) {
			defers.put(Registry.ITEM_REGISTRY.location(), () -> createItemBlock(block));
		}

		if(block instanceof IZetaBlockColorProvider)
			blockColors.put(block, (IZetaBlockColorProvider) block);
	}

	public void setCreativeTab(Block block, CreativeModeTab group) {
		ResourceLocation res = getInternalName(block);
		if(res == null)
			throw new IllegalArgumentException("Can't set the creative tab for a block without a registry name yet");

		groups.put(res, group);
	}

	private Item createItemBlock(Block block) {
		Item.Properties props = new Item.Properties();
		ResourceLocation registryName = getInternalName(block);

		CreativeModeTab group = groups.get(registryName);
		if(group != null)
			props = props.tab(group);

		if(block instanceof IZetaItemPropertiesFiller)
			((IZetaItemPropertiesFiller) block).fillItemProperties(props);

		BlockItem blockitem;
		if(block instanceof IZetaBlockItemProvider)
			blockitem = ((IZetaBlockItemProvider) block).provideItemBlock(block, props);
		else blockitem = new BlockItem(block, props);

		if(block instanceof IZetaItemColorProvider)
			itemColors.put(blockitem, (IZetaItemColorProvider) block);

		setInternalName(blockitem, registryName);
		return blockitem;
	}

	/// performing registration ///

	public Collection<Supplier<Object>> getDefers(ResourceLocation registryId) {
		return defers.get(registryId);
	}

	public void clearDeferCache(ResourceLocation resourceLocation) {
		defers.removeAll(resourceLocation);
	}

	//TODO: sided woes
	public void submitBlockColors(BiConsumer<BlockColor, Block> consumer) {
		blockColors.forEach((k, v) -> consumer.accept(v.getBlockColor(), k));
		blockColors.clear();
	}

	//TODO: sided woes
	public void submitItemColors(BiConsumer<ItemColor, Item> consumer) {
		itemColors.forEach((k, v) -> consumer.accept(v.getItemColor(), k));
		itemColors.clear();
	}
}
