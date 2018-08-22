package vazkii.quark.world.feature;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.quark.base.handler.DimensionConfig;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.world.FairyRingGenerator;

public class FairyRings extends Feature {

	public static int forestChance, plainsChance;
	public static DimensionConfig dimensions;
	public static List<Block> ores;
	
	boolean initted = false;
	String[] oresArr;
	
	@Override
	public void setupConfig() {
		forestChance = loadPropInt("Forest Chance", "", 40);
		plainsChance = loadPropInt("Plains Chance", "", 100);
		dimensions = new DimensionConfig(configCategory, "0");
		
		oresArr = loadPropStringList("Spawnable Ores", "", new String[] {
			Blocks.EMERALD_ORE.getRegistryName().toString(),
			Blocks.DIAMOND_ORE.getRegistryName().toString()
		});
		if(initted)
			loadOres();
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		GameRegistry.registerWorldGenerator(new FairyRingGenerator(), 100);
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		loadOres();
	}
	
	private void loadOres() {
		ores = new ArrayList(oresArr.length);
		for(String s : oresArr) {
			Block b = Block.getBlockFromName(s);
			if(b == null)
				new IllegalArgumentException("Block " + s + " does not exist!").printStackTrace();
			else ores.add(b);
		}
			
		initted = true;
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
