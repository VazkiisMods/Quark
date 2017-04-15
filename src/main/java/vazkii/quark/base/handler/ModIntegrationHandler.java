package vazkii.quark.base.handler;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public final class ModIntegrationHandler {

	public static void addCharsetCarry(Block b) {
		FMLInterModComms.sendMessage("charsetlib", "addCarry", b.getRegistryName());
	}
	
}
