package vazkii.quark.content.building.module;

import net.minecraft.world.level.block.Block;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.building.block.SturdyStoneBlock;

@LoadModule(category = "building")
public class SturdyStoneModule extends QuarkModule {

	@Hint Block sturdy_stone;
	
	@Override
	public void register() {
		sturdy_stone = new SturdyStoneBlock(this);
	}
	
}
