package org.violetmoon.zeta.client;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.client.event.load.ZAddBlockColorHandlers;
import org.violetmoon.zeta.client.event.load.ZAddItemColorHandlers;
import org.violetmoon.zeta.client.event.load.ZClientSetup;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.registry.DyeablesRegistry;
import org.violetmoon.zeta.registry.RenderLayerRegistry;
import org.violetmoon.zeta.registry.ZetaRegistry;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Just a grab-bag of stuff that needs to be done on the physical client
 */
public abstract class ClientRegistryExtension {
	protected final Zeta z;
	protected final ZetaRegistry registry;

	protected final Map<RenderLayerRegistry.Layer, RenderType> resolvedTypes = new EnumMap<>(RenderLayerRegistry.Layer.class);

	public ClientRegistryExtension(Zeta z) {
		this.z = z;
		this.registry = z.registry;

		resolvedTypes.put(RenderLayerRegistry.Layer.SOLID, RenderType.solid());
		resolvedTypes.put(RenderLayerRegistry.Layer.CUTOUT, RenderType.cutout());
		resolvedTypes.put(RenderLayerRegistry.Layer.CUTOUT_MIPPED, RenderType.cutoutMipped());
		resolvedTypes.put(RenderLayerRegistry.Layer.TRANSLUCENT, RenderType.translucent());
	}

	@LoadEvent
	public void registerItemColorHandlers(ZAddItemColorHandlers event) {
		DyeablesRegistry dyeables = z.dyeables;

		ClampedItemPropertyFunction isDyed = (stack, level, entity, i) -> dyeables.isDyed(stack) ? 1 : 0;
		ItemColor color = (stack, layer) -> layer == 0 ? dyeables.getColor(stack) : 0xFF_FF_FF;
		//apparently ItemPropertyFunctions are weird and can only be assigned to the minecraft: namespace
		ResourceLocation isDyedId = new ResourceLocation("minecraft", z.modid + "_dyed");

		for(Item item : dyeables.dyeableConditions.keySet()) {
			ItemProperties.register(item, isDyedId, isDyed);
			event.register(color, item);
		}
	}

	//these are in Post events to give other listeners a chance to populate .registerNamed
	@LoadEvent
	public void registerBlockColorsPost(ZAddBlockColorHandlers.Post event) {
		registry.finalizeBlockColors((block, name) -> {
			Function<Block, BlockColor> blockColorCreator = event.getNamedBlockColors().get(name);
			if(blockColorCreator == null)
				z.log.error("Unknown block color creator {} used on block {}", name, block);
			else
				event.register(blockColorCreator.apply(block), block);
		});
	}

	@LoadEvent
	public void registerItemColorsPost(ZAddItemColorHandlers.Post event) {
		registry.finalizeItemColors((item, name) -> {
			Function<Item, ItemColor> itemColorCreator = event.getNamedItemColors().get(name);
			if(itemColorCreator == null)
				z.log.error("Unknown item color creator {} used on item {}", name, item);
			else
				event.register(itemColorCreator.apply(item), item);
		});
	}

	@LoadEvent
	public void registerRenderLayers(ZClientSetup event) {
		z.renderLayerRegistry.finalize(this::doSetRenderLayer);
	}

	protected abstract void doSetRenderLayer(Block block, RenderLayerRegistry.Layer layer);
}
