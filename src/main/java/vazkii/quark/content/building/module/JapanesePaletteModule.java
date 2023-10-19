/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [24/03/2016, 03:18:35 (GMT)]
 */
package vazkii.quark.content.building.module;

import java.util.function.BooleanSupplier;

import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.building.block.BambooMatBlock;
import vazkii.quark.content.building.block.BambooMatCarpetBlock;
import vazkii.quark.content.building.block.PaperLanternBlock;
import vazkii.quark.content.building.block.PaperWallBlock;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "building")
public class JapanesePaletteModule extends QuarkModule {

	@Config(flag = "paper_decor")
	public static boolean enablePaperBlocks = true;
	
	@Config(flag = "bamboo_mat")
	public static boolean enableBambooMats = true;
	
	@LoadEvent
	public final void register(ZRegister event) {
		BooleanSupplier paperBlockCond = () -> enablePaperBlocks;
		BooleanSupplier bambooMatCond = () -> enableBambooMats;

		IQuarkBlock parent = new PaperLanternBlock("paper_lantern", this).setCondition(paperBlockCond);
		new PaperLanternBlock("paper_lantern_sakura", this).setCondition(paperBlockCond);

		new PaperWallBlock(parent, "paper_wall").setCondition(paperBlockCond);
		new PaperWallBlock(parent, "paper_wall_big").setCondition(paperBlockCond);
		new PaperWallBlock(parent, "paper_wall_sakura").setCondition(paperBlockCond);
		
		new BambooMatBlock("bamboo_mat", this).setCondition(bambooMatCond);
		new BambooMatCarpetBlock("bamboo_mat_carpet", this).setCondition(bambooMatCond);
	}
	
}

