package vazkii.quark.content.building.module;

import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import vazkii.quark.base.block.QuarkPaneBlock;
import vazkii.quark.base.handler.StructureBlockReplacementHandler;
import vazkii.quark.base.handler.StructureBlockReplacementHandler.StructureHolder;
import vazkii.quark.base.module.config.Config;
import vazkii.zeta.event.ZConfigChanged;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;
import vazkii.zeta.registry.RenderLayerRegistry;

@ZetaLoadModule(category = "building")
public class GoldBarsModule extends ZetaModule {

	@Config public static boolean generateInNetherFortress = true;

	public static boolean staticEnabled;

	public static Block gold_bars;

	@LoadEvent
	public final void register(ZRegister event) {
		gold_bars = new QuarkPaneBlock("gold_bars", this, Properties.copy(Blocks.IRON_BARS), RenderLayerRegistry.Layer.CUTOUT);

		StructureBlockReplacementHandler.addReplacement(GoldBarsModule::getGenerationBarBlockState);
	}

	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
		staticEnabled = enabled;
	}

	private static BlockState getGenerationBarBlockState(ServerLevelAccessor accessor, BlockState current, StructureHolder structure) {
		if(staticEnabled && generateInNetherFortress 
				&& current.getBlock() == Blocks.NETHER_BRICK_FENCE 
				&& StructureBlockReplacementHandler.isStructure(accessor, structure, BuiltinStructures.FORTRESS)) {
			
			return gold_bars.withPropertiesOf(current);
		}

		return null; // no change
	}


}
