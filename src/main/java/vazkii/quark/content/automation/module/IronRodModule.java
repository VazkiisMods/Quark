package vazkii.quark.content.automation.module;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.automation.block.IronRodBlock;

@LoadModule(category = "automation")
public class IronRodModule extends QuarkModule {

	public static TagKey<Block> ironRodImmuneTag;

	@Config(flag = "iron_rod_pre_end")
	public static boolean usePreEndRecipe = false;

	@Hint public static Block iron_rod;

	@Override
	public void register() {
		iron_rod = new IronRodBlock(this);
	}

	@Override
	public void setup() {
		ironRodImmuneTag = BlockTags.create(new ResourceLocation(Quark.MOD_ID, "iron_rod_immune"));
	}
}
