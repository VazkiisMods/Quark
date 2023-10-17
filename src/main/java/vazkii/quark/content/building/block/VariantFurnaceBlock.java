package vazkii.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.base.Quark;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.handler.CreativeTabHandler;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.building.block.be.VariantFurnaceBlockEntity;
import vazkii.quark.content.building.module.VariantFurnacesModule;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;

public class VariantFurnaceBlock extends FurnaceBlock implements IQuarkBlock {

	private final QuarkModule module;

	public VariantFurnaceBlock(String type, QuarkModule module, Properties props) {
		super(props);

		Quark.ZETA.registry.registerBlock(this, type + "_furnace", true);
		CreativeTabHandler.addTab(this, CreativeModeTab.TAB_DECORATIONS);

		this.module = module;
	}

	@Override
	public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
		return new VariantFurnaceBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level world, @Nonnull BlockState state, @Nonnull BlockEntityType<T> beType) {
		return createFurnaceTicker(world, beType, VariantFurnacesModule.blockEntityType);
	}

	@Override
	protected void openContainer(Level world, @Nonnull BlockPos pos, @Nonnull Player player) {
		BlockEntity blockentity = world.getBlockEntity(pos);
		if(blockentity instanceof AbstractFurnaceBlockEntity furnace) {
			player.openMenu(furnace);
			player.awardStat(Stats.INTERACT_WITH_FURNACE);
		}
	}

	@Override
	public QuarkModule getModule() {
		return module;
	}

	@Override
	public IQuarkBlock setCondition(BooleanSupplier condition) {
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return true;
	}

}
