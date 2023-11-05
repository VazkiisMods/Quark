package vazkii.quark.content.building.module;

import net.minecraft.world.level.block.Block;
import vazkii.quark.content.building.block.GrateBlock;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;
import vazkii.zeta.util.Hint;

/**
 * @author WireSegal
 * Created at 8:57 AM on 8/27/19.
 */
@ZetaLoadModule(category = "building")
public class GrateModule extends ZetaModule {

	@Hint public static Block grate;
	
	@LoadEvent
	public final void register(ZRegister event) {
		grate = new GrateBlock(this);
	}
	
}
