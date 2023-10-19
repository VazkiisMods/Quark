package vazkii.quark.content.building.module;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.base.util.CorundumColor;
import vazkii.quark.content.building.block.RainbowLampBlock;
import vazkii.quark.content.world.module.CorundumModule;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "building")
public class RainbowLampsModule extends QuarkModule {

	@Config
	public static int lightLevel = 15;

	@Config(description = "Whether Rainbow Lamps should be made from and themed on Corundum if that module is enabled.", flag = "rainbow_lamp_corundum")
	public static boolean useCorundum = true;

	@Hint("crystal_lamp")
	public static TagKey<Block> lampTag;

	public static boolean isCorundum() {
		return CorundumModule.staticEnabled && useCorundum;
	}

	@LoadEvent
	public final void setup(ZCommonSetup event) {
		lampTag = BlockTags.create(new ResourceLocation(Quark.MOD_ID, "crystal_lamp"));
	}

	@LoadEvent
	public final void register(ZRegister event) {
		for(CorundumColor color : CorundumColor.values())
			new RainbowLampBlock(color.name + "_crystal_lamp", color.beaconColor, this, color.materialColor);
	}
}
