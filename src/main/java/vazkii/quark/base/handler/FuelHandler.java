package vazkii.quark.base.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.base.Quark;
import vazkii.quark.content.building.block.VerticalSlabBlock;
import vazkii.zeta.event.ZLoadComplete;
import vazkii.zeta.event.bus.LoadEvent;

public class FuelHandler {

	private static final Map<Item, Integer> fuelValues = new HashMap<>();

	public static void addFuel(Item item, int fuel) {
		if(fuel > 0 && item != null && !fuelValues.containsKey(item))
			fuelValues.put(item, fuel);
	}

	public static void addFuel(Block block, int fuel) {
		addFuel(block.asItem(), fuel);
	}

	public static void addWood(Block block) {
		String regname = Objects.toString(Quark.ZETA.registry.getRegistryName(block, Registry.BLOCK));
		if(regname.contains("crimson") || regname.contains("warped"))
			return; //do nothing if block is crimson or warped, since they aren't flammable. #3549
		if(block instanceof VerticalSlabBlock || block instanceof SlabBlock)
			addFuel(block, 150);
		else addFuel(block, 300);
	}

	@LoadEvent
	public static void addAllWoods(ZLoadComplete event) {
		for(Block block : ForgeRegistries.BLOCKS) {
			ResourceLocation regname = Quark.ZETA.registry.getRegistryName(block, Registry.BLOCK);
			if(block != null && regname.getNamespace().equals(Quark.MOD_ID) && block.defaultBlockState().getMaterial() == Material.WOOD)
				addWood(block);
		}
	}

	@SubscribeEvent
	public static void getFuel(FurnaceFuelBurnTimeEvent event) {
		Item item = event.getItemStack().getItem();
		if(fuelValues.containsKey(item))
			event.setBurnTime(fuelValues.get(item));
	}

}
