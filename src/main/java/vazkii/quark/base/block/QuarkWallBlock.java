package vazkii.quark.base.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.CreativeTabHandler;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.zeta.module.ZetaModule;
import vazkii.zeta.registry.IZetaBlockColorProvider;
import vazkii.zeta.registry.IZetaItemColorProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

public class QuarkWallBlock extends WallBlock implements IQuarkBlock, IZetaBlockColorProvider {

	private final IQuarkBlock parent;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkWallBlock(IQuarkBlock parent) {
		super(VariantHandler.realStateCopy(parent));

		this.parent = parent;
		String resloc = IQuarkBlock.inheritQuark(parent, "%s_wall");
		Quark.ZETA.registry.registerBlock(this, resloc, true);
		CreativeTabHandler.addTab(this, CreativeModeTab.TAB_DECORATIONS);

		Quark.ZETA.renderLayerRegistry.mock(this, parent.getBlock());
	}

	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if(parent.isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Nullable
	@Override
	public ZetaModule getModule() {
		return parent.getModule();
	}

	@Override
	public QuarkWallBlock setCondition(BooleanSupplier enabledSupplier) {
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
		BlockState parentState = parent.getBlock().defaultBlockState();
		return Quark.ZETA.blockExtensions.get(parentState).getBeaconColorMultiplierZeta(parentState, world, pos, beaconPos);
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
