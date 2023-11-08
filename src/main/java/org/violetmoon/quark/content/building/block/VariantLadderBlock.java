package org.violetmoon.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.block.IQuarkBlock;
import org.violetmoon.quark.base.handler.CreativeTabHandler;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.RenderLayerRegistry;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;

public class VariantLadderBlock extends LadderBlock implements IQuarkBlock {

	private final ZetaModule module;
	private final boolean flammable;

	private BooleanSupplier condition = () -> true;

	public VariantLadderBlock(String type, ZetaModule module, Block.Properties props, boolean flammable) {
		super(props);

		Quark.ZETA.registry.registerBlock(this, type + "_ladder", true);
		CreativeTabHandler.addTab(this, CreativeModeTab.TAB_DECORATIONS);

		this.module = module;
		module.zeta.renderLayerRegistry.put(this, RenderLayerRegistry.Layer.CUTOUT);

		this.flammable = flammable;
	}

	public VariantLadderBlock(String type, ZetaModule module, boolean flammable) {
		this(type, module,
				Block.Properties.copy(Blocks.LADDER),
			flammable);
	}

	@Override
	public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return flammable;
	}

	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Override
	public ZetaModule getModule() {
		return module;
	}

	@Override
	public VariantLadderBlock setCondition(BooleanSupplier condition) {
		this.condition = condition;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return condition.getAsBoolean();
	}

}
