package org.violetmoon.quark.content.automation.module;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.automation.block.IronRodBlock;
import org.violetmoon.zeta.config.Config;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.Hint;

@ZetaLoadModule(category = "automation")
public class IronRodModule extends ZetaModule {

	public static TagKey<Block> ironRodImmuneTag;

	@Config(flag = "iron_rod_pre_end")
	public static boolean usePreEndRecipe = false;

	@Hint
	public static Block iron_rod;

	@LoadEvent
	public final void register(ZRegister event) {
		iron_rod = new IronRodBlock(this);
	}

	@LoadEvent
	public final void setup(ZCommonSetup event) {
		ironRodImmuneTag = BlockTags.create(new ResourceLocation(Quark.MOD_ID, "iron_rod_immune"));
	}
}
