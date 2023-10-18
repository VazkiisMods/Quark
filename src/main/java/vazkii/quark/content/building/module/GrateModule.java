package vazkii.quark.content.building.module;

import net.minecraft.world.level.block.Block;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.building.block.GrateBlock;

/**
 * @author WireSegal
 * Created at 8:57 AM on 8/27/19.
 */
@LoadModule(category = "building")
public class GrateModule extends QuarkModule {

	@Hint public static Block grate;
	
	@Override
	public void register() {
		grate = new GrateBlock(this);
	}
	
}
