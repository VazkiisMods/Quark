package vazkii.arl.block;

import net.minecraft.world.level.block.Block;
import vazkii.arl.util.RegistryHelper;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BasicBlock extends Block {

	public BasicBlock(String regname, Properties properties) {
		super(properties);
		
		RegistryHelper.registerBlock(this, regname);
	}

}
