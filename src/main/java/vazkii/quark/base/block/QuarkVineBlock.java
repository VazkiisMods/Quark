package vazkii.quark.base.block;

import java.util.function.BooleanSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import vazkii.quark.base.handler.CreativeTabHandler;
import vazkii.zeta.module.ZetaModule;
import vazkii.zeta.registry.RenderLayerRegistry;

public class QuarkVineBlock extends VineBlock implements IQuarkBlock {

	private final ZetaModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkVineBlock(ZetaModule module, String name, boolean creative) {
		super(Block.Properties.of(Material.REPLACEABLE_PLANT).noCollission().randomTicks().strength(0.2F).sound(SoundType.GRASS));
		this.module = module;

		module.zeta.registry.registerBlock(this, name, true);
		module.zeta.renderLayerRegistry.put(this, RenderLayerRegistry.Layer.CUTOUT);

		if (creative) CreativeTabHandler.addTab(this, CreativeModeTab.TAB_DECORATIONS);
	}

	@Override
	public void randomTick(@Nonnull BlockState state, @Nonnull ServerLevel worldIn, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
		tick(state, worldIn, pos, random);
	}

	@Override
	public boolean isFlammableZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return true;
	}

	@Override
	public int getFlammabilityZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 300;
	}

	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Nullable
	@Override
	public ZetaModule getModule() {
		return module;
	}

	@Override
	public QuarkVineBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

}
