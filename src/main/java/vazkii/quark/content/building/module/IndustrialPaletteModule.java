package vazkii.quark.content.building.module;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.util.ForgeSoundType;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.block.QuarkPillarBlock;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.building.block.VariantLadderBlock;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;

import java.util.function.BooleanSupplier;

@LoadModule(category = "building")
public class IndustrialPaletteModule extends QuarkModule {

	private static final SoundType IRON_LADDER_SOUND_TYPE = new ForgeSoundType(1.0F, 1.0F,
			() -> SoundEvents.METAL_BREAK,
			() -> SoundEvents.LADDER_STEP,
			() -> SoundEvents.METAL_PLACE,
			() -> SoundEvents.METAL_HIT,
			() -> SoundEvents.LADDER_FALL);

	@Config(flag = "iron_plates")
	public static boolean enableIronPlates = true;

	@Config(flag = "iron_ladder")
	public static boolean enableIronLadder = true;

	@LoadEvent
	public final void register(ZRegister event) {
		Block.Properties props = Block.Properties.copy(Blocks.IRON_BLOCK);

		BooleanSupplier ironPlateCond = () -> enableIronPlates;
		BooleanSupplier ironLadderCond = () -> enableIronLadder;

		VariantHandler.addSlabAndStairs(new QuarkBlock("iron_plate", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props).setCondition(ironPlateCond));
		VariantHandler.addSlabAndStairs(new QuarkBlock("rusty_iron_plate", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props).setCondition(ironPlateCond));

		new QuarkPillarBlock("iron_pillar", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props).setCondition(ironPlateCond);

		new VariantLadderBlock("iron", this, Block.Properties.of(Material.DECORATION)
				.strength(0.8F)
				.sound(IRON_LADDER_SOUND_TYPE)
				.noOcclusion(), false)
		.setCondition(ironLadderCond);
	}

}
