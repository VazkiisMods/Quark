package org.violetmoon.quark.content.building.module;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.util.CorundumColor;
import org.violetmoon.quark.content.building.block.RainbowLampBlock;
import org.violetmoon.quark.content.world.module.CorundumModule;
import org.violetmoon.zeta.config.Config;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.Hint;

@ZetaLoadModule(category = "building")
public class RainbowLampsModule extends ZetaModule {

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
			new RainbowLampBlock(color.name + "_crystal_lamp", color.beaconColor, this, color.mapColor);
	}
}
