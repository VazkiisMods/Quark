package vazkii.quark.base.block;

import java.util.function.BooleanSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.CreativeTabHandler;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.module.QuarkModule;
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

		RenderLayerHandler.setInherited(this, parent.getBlock());
	}

	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}
	
	@Override
	public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return parent.isFlammable(state, world, pos, face);
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return parent.getFlammability(state, world, pos, face);
	}
	

	@Nullable
	@Override
	public QuarkModule getModule() {
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
	public float[] getBeaconColorMultiplier(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
		return parent.getBlock().getBeaconColorMultiplier(parent.getBlock().defaultBlockState(), world, pos, beaconPos);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockColor getBlockColor() {
		return parent instanceof IZetaBlockColorProvider provider ? provider.getBlockColor() : null;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemColor getItemColor() {
		return parent instanceof IZetaItemColorProvider provider ? provider.getItemColor() : null;
	}
}
