package vazkii.quark.content.automation.module;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.automation.block.IronRodBlock;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;
import vazkii.zeta.util.Hint;

@ZetaLoadModule(category = "automation")
public class IronRodModule extends ZetaModule {

	public static TagKey<Block> ironRodImmuneTag;

	@Config(flag = "iron_rod_pre_end")
	public static boolean usePreEndRecipe = false;

	@Hint public static Block iron_rod;

	@LoadEvent
	public final void register(ZRegister event) {
		iron_rod = new IronRodBlock(this);
	}

	@LoadEvent
	public final void setup(ZCommonSetup event) {
		ironRodImmuneTag = BlockTags.create(new ResourceLocation(Quark.MOD_ID, "iron_rod_immune"));
	}
}
