package vazkii.quark.base.block;

import java.util.function.BooleanSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.CreativeTabHandler;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.zeta.module.ZetaModule;
import vazkii.zeta.registry.IZetaBlockColorProvider;
import vazkii.zeta.registry.IZetaItemColorProvider;

public class QuarkStairsBlock extends StairBlock implements IQuarkBlock, IZetaBlockColorProvider {

	private final IQuarkBlock parent;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkStairsBlock(IQuarkBlock parent) {
		super(parent.getBlock()::defaultBlockState, VariantHandler.realStateCopy(parent));

		this.parent = parent;
		String resloc = IQuarkBlock.inheritQuark(parent, "%s_stairs");
		Quark.ZETA.registry.registerBlock(this, resloc, true);

		CreativeTabHandler.addTab(this, CreativeModeTab.TAB_BUILDING_BLOCKS);

		Quark.ZETA.renderLayerRegistry.mock(this, parent.getBlock());
	}

	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Override
	public boolean isFlammableZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		BlockState parentState = parent.getBlock().defaultBlockState();
		return Quark.ZETA.blockExtensions.get(parentState).isFlammableZeta(parentState, world, pos, face);
	}

	@Override
	public int getFlammabilityZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		BlockState parentState = parent.getBlock().defaultBlockState();
		return Quark.ZETA.blockExtensions.get(parentState).getFlammabilityZeta(parentState, world, pos, face);
	}

	@Nullable
	@Override
	public ZetaModule getModule() {
		return parent.getModule();
	}

	@Override
	public QuarkStairsBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

	@Nullable
	@Override
	public float[] getBeaconColorMultiplierZeta(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
		return Quark.ZETA.blockExtensions.get(state).getBeaconColorMultiplierZeta(state, world, pos, beaconPos);
	}

	@Override
	public @Nullable String getBlockColorProviderName() {
		return parent instanceof IZetaBlockColorProvider prov ? prov.getBlockColorProviderName() : null;
	}

	@Override
	public @Nullable String getItemColorProviderName() {
		return parent instanceof IZetaItemColorProvider prov ? prov.getItemColorProviderName() : null;
	}

}
