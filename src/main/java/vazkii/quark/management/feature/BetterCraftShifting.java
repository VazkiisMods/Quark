package vazkii.quark.management.feature;

import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;

public class BetterCraftShifting extends Feature {

	public static boolean enableCraftingTable, enableVillager;
	
	@Override
	public void setupConfig() {
		enableCraftingTable = loadPropBool("Enable Crafting Table", "", true);
		enableVillager = loadPropBool("Enable Villager", "", true);
	}
	
	public static int getInventoryBoundaryCrafting(int curr) {
		if(!ModuleLoader.isFeatureEnabled(BetterCraftShifting.class) || !enableCraftingTable)
			return curr;
		
		return curr == 37 ? 0 : 10;
	}
	
	public static int getInventoryBoundaryVillager(int curr) {
		if(!ModuleLoader.isFeatureEnabled(BetterCraftShifting.class) || !enableVillager)
			return curr;
		
		return curr == 30 ? 0 : 1;
	}
	
}
