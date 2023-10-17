package vazkii.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import vazkii.quark.base.Quark;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.handler.CreativeTabHandler;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.building.block.be.VariantTrappedChestBlockEntity;
import vazkii.quark.content.building.module.VariantChestsModule.IChestTextureProvider;
import vazkii.zeta.registry.IZetaBlockItemProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@OnlyIn(value = Dist.CLIENT, _interface = IZetaBlockItemProvider.class)
public class VariantTrappedChestBlock extends ChestBlock implements IZetaBlockItemProvider, IQuarkBlock, IChestTextureProvider {

	public final String type;
	private final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	private final String path;

	public VariantTrappedChestBlock(String prefix, String type, QuarkModule module, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier, Properties props) {
		super(props, supplier);
		String resloc = (prefix != null ? prefix + "_" : "") + type + "_trapped_chest";
		Quark.ZETA.registry.registerBlock(this, resloc, true);

		CreativeTabHandler.addTab(this, CreativeModeTab.TAB_REDSTONE);

		this.type = type;
		this.module = module;

		path = (isCompat() ? "compat/" : "") + type + "/";
	}

	public VariantTrappedChestBlock(String type, QuarkModule module, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier, Properties props) {
		this(null, type, module, supplier, props);
	}

	protected boolean isCompat() {
		return false;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return 0;
	}

	@Override
	public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return false;
	}

	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Override
	public VariantTrappedChestBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

	@Nullable
	@Override
	public QuarkModule getModule() {
		return module;
	}

	@Override
	public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState p_153065_) {
		return new VariantTrappedChestBlockEntity(pos, p_153065_);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockItem provideItemBlock(Block block, Item.Properties props) {
		return new VariantChestBlock.Item(block, props);
	}

	public static class Compat extends VariantTrappedChestBlock {

		public Compat(String type, String mod, QuarkModule module, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier, Properties props) {
			super(type, module, supplier, props);
			setCondition(() -> ModList.get().isLoaded(mod));
		}

		@Override
		protected boolean isCompat() {
			return true;
		}
	}

	@Override
	public String getChestTexturePath() {
		return "model/chest/" + path;
	}

	@Override
	public boolean isTrap() {
		return true;
	}

	// VANILLA TrappedChestBlock copy

	@Nonnull
	@Override
	protected Stat<ResourceLocation> getOpenChestStat() {
		return Stats.CUSTOM.get(Stats.TRIGGER_TRAPPED_CHEST);
	}

	@Override
	public boolean isSignalSource(@Nonnull BlockState state) {
		return true;
	}

	@Override
	public int getSignal(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull Direction side) {
		return Mth.clamp(ChestBlockEntity.getOpenCount(world, pos), 0, 15);
	}

	@Override
	public int getDirectSignal(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull Direction side) {
		return side == Direction.UP ? state.getSignal(world, pos, side) : 0;
	}

}
