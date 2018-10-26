/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [02/07/2016, 23:09:15 (GMT)]
 */
package vazkii.quark.world.feature;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.DimensionConfig;
import vazkii.quark.base.lib.LibEntityIDs;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.client.render.RenderPirate;
import vazkii.quark.world.entity.EntityPirate;
import vazkii.quark.world.item.ItemPirateHat;
import vazkii.quark.world.world.PirateShipGenerator;

public class PirateShips extends Feature {

	public static final ResourceLocation PIRATE_CHEST_LOOT_TABLE = new ResourceLocation("quark", "chests/pirate_chest");
	public static final ResourceLocation SHIP_STRUCTURE = new ResourceLocation("quark", "pirate_ship");

	public static Item pirate_hat;

	boolean onlyHat;
	public static int rarity;
	DimensionConfig dims;

	@Override
	public void setupConfig() {
		onlyHat = loadPropBool("Only hat", "Disables the pirate mob and generator, only adds the hat", false);
		rarity = loadPropInt("Pirate Ship Rarity", "Given this value as X, 1 ship will spawn in X ocean biome chunks", 4000);
		dims = new DimensionConfig(configCategory);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		pirate_hat = new ItemPirateHat();

		if(onlyHat)
			return;
		
		String pirateName = "quark:pirate";
		EntityRegistry.registerModEntity(new ResourceLocation(pirateName), EntityPirate.class, pirateName, LibEntityIDs.PIRATE, Quark.instance, 80, 3, true, 0x4d1d14, 0xac9617);

		GameRegistry.registerWorldGenerator(new PirateShipGenerator(dims), 0);
		LootTableList.register(PIRATE_CHEST_LOOT_TABLE);
	}

	@Override
	public void preInitClient(FMLPreInitializationEvent event) {
		if(!onlyHat)
			RenderingRegistry.registerEntityRenderingHandler(EntityPirate.class, RenderPirate.FACTORY);
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
