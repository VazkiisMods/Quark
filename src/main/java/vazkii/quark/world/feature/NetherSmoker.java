package vazkii.quark.world.feature;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.tile.TileCustomChest;
import vazkii.quark.world.block.BlockSmoker;
import vazkii.quark.world.tile.TileSmoker;
import vazkii.quark.world.world.ClayGenerator;
import vazkii.quark.world.world.SmokerGenerator;

public class NetherSmoker extends Feature {

	public static Block smoker;
	
	public static float chunkChance = 0.4F;
	public static int triesPerChunk = 20;
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		smoker = new BlockSmoker();
		
		registerTile(TileSmoker.class, "smoker");
		
		GameRegistry.registerWorldGenerator(new SmokerGenerator(), 0);
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
