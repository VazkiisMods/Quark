package vazkii.quark.building.feature;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.quark.base.handler.RecipeHandler;
import vazkii.quark.base.module.Feature;

public class StainedHardenedClay extends Feature{

	String stainedDyes[] = {"dyeWhite", "dyeOrange", "dyeMagenta", "dyeLightBlue", "dyeYellow", "dyeLime", "dyePink", "dyeGray", "dyeLightGray", "dyeCyan", "dyePurple", "dyeBlue", "dyeBrown", "dyeGreen", "dyeRed", "dyeBlack"};
	
	boolean enableStainedHardnedClay;
	
	@Override
	public void setupConfig() {
		enableStainedHardnedClay = loadPropBool("Enable stained hard clay recipes", "", true);
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
	
		if(enableStainedHardnedClay){
			for (int i = 0; i < 16; i++){
				OreDictionary.registerOre("stainedHardnedClay", new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, i));
				RecipeHandler.addOreDictRecipe(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 8, i), "ccc", "cdc", "ccc", 'c', "stainedHardnedClay", 'd', stainedDyes[i]);
		 	 }
		}
	}
}
