package vazkii.quark.content.building.module;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.base.handler.FuelHandler;
import vazkii.quark.base.handler.ItemOverrideHandler;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.util.VanillaWoods;
import vazkii.quark.base.util.VanillaWoods.Wood;
import vazkii.quark.content.building.block.VariantLadderBlock;
import vazkii.zeta.event.ZConfigChanged;
import vazkii.zeta.event.ZLoadComplete;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;

import java.util.LinkedList;
import java.util.List;

@ZetaLoadModule(category = "building", antiOverlap = { "woodworks" })
public class VariantLaddersModule extends ZetaModule {

	@Config public static boolean changeNames = true;

	public static List<Block> variantLadders = new LinkedList<>();

	public static boolean moduleEnabled;

	@LoadEvent
	public final void register(ZRegister event) {
		for(Wood type : VanillaWoods.NON_OAK)
			variantLadders.add(new VariantLadderBlock(type.name(), this, !type.nether()));
	}

	@LoadEvent
	public void loadComplete(ZLoadComplete e) {
		variantLadders.forEach(FuelHandler::addWood);
	}

	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
		moduleEnabled = this.enabled;
		ItemOverrideHandler.changeBlockLocalizationKey(Blocks.LADDER, "block.quark.oak_ladder", changeNames && enabled);
	}

	public static boolean isTrapdoorLadder(boolean defaultValue, LevelReader world, BlockPos pos) {
		if(defaultValue || !moduleEnabled)
			return defaultValue;

		BlockState curr = world.getBlockState(pos);
		if(curr.getProperties().contains(TrapDoorBlock.OPEN) && curr.getValue(TrapDoorBlock.OPEN)) {
			BlockState down = world.getBlockState(pos.below());
			if(down.getBlock() instanceof LadderBlock)
				return down.getValue(LadderBlock.FACING) == curr.getValue(TrapDoorBlock.FACING);
		}

		return false;
	}

}
