package vazkii.quark.base.block;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.base.Quark;
import vazkii.zeta.registry.IZetaBlockColorProvider;
import vazkii.zeta.registry.IZetaItemColorProvider;

/**
 * @author WireSegal
 * Created at 1:09 PM on 9/19/19.
 */
public class QuarkInheritedPaneBlock extends QuarkPaneBlock implements IQuarkBlock, IZetaBlockColorProvider {

	public final IQuarkBlock parent;

	public QuarkInheritedPaneBlock(IQuarkBlock parent, String name, Block.Properties properties) {
		super(name, parent.getModule(), properties, null);

		this.parent = parent;
		Quark.ZETA.renderLayerRegistry.mock(this, parent.getBlock());
	}

	public QuarkInheritedPaneBlock(IQuarkBlock parent, Block.Properties properties) {
		this(parent, IQuarkBlock.inheritQuark(parent, "%s_pane"), properties);
	}

	public QuarkInheritedPaneBlock(IQuarkBlock parent) {
		this(parent, Block.Properties.copy(parent.getBlock()));
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled() && parent.isEnabled();
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
