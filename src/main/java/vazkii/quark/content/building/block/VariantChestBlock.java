package vazkii.quark.content.building.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
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
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.ModList;
import vazkii.quark.base.Quark;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.handler.CreativeTabHandler;
import vazkii.zeta.module.ZetaModule;
import vazkii.quark.content.building.block.be.VariantChestBlockEntity;
import vazkii.quark.content.building.module.VariantChestsModule.IChestTextureProvider;
import vazkii.zeta.registry.IZetaBlockItemProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

@OnlyIn(value = Dist.CLIENT, _interface = IZetaBlockItemProvider.class)
public class VariantChestBlock extends ChestBlock implements IZetaBlockItemProvider, IQuarkBlock, IChestTextureProvider {

	private final ZetaModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	private final String path;

	public VariantChestBlock(String prefix, String type, ZetaModule module, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier, Properties props) {
		super(props, supplier);
		String resloc = (prefix != null ? prefix + "_" : "") + type + "_chest";
		Quark.ZETA.registry.registerBlock(this, resloc, true);
		CreativeTabHandler.addTab(this, CreativeModeTab.TAB_DECORATIONS);

		this.module = module;

		path = (isCompat() ? "compat/" : "") + type + "/";
	}

	public VariantChestBlock(String type, ZetaModule module, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier, Properties props) {
		this(null, type, module, supplier, props);
	}

	protected boolean isCompat() {
		return false;
	}

	@Override
	public int getFlammabilityZeta(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return 0;
	}

	@Override
	public boolean isFlammableZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return false;
	}

	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Override
	public VariantChestBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

	@Nullable
	@Override
	public ZetaModule getModule() {
		return module;
	}

	@Override
	public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
		return new VariantChestBlockEntity(pos, state);
	}

	@Override
	public String getChestTexturePath() {
		return "model/chest/" + path;
	}

	@Override
	public boolean isTrap() {
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockItem provideItemBlock(Block block, Item.Properties props) {
		return new Item(block, props);
	}

	public static class Item extends BlockItem {

		public Item(Block block, Properties props) {
			super(block, props);
		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public void initializeClient(Consumer<IClientItemExtensions> consumer) {
			consumer.accept(new IClientItemExtensions() {

				@Override
				public BlockEntityWithoutLevelRenderer getCustomRenderer() {
					Minecraft mc = Minecraft.getInstance();

					return new BlockEntityWithoutLevelRenderer(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels()) {
						private final BlockEntity tile = new VariantChestBlockEntity(BlockPos.ZERO, getBlock().defaultBlockState());

						@Override
						public void renderByItem(@Nonnull ItemStack stack, @Nonnull TransformType transformType, @Nonnull PoseStack pose, @Nonnull MultiBufferSource buffer, int x, int y) {
							mc.getBlockEntityRenderDispatcher().renderItem(tile, pose, buffer, x, y);
						}

					};
				}

			});
		}
	}

	public static class Compat extends VariantChestBlock {

		public Compat(String type, String mod, ZetaModule module, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier, Properties props) {
			super(type, module, supplier, props);
			setCondition(() -> ModList.get().isLoaded(mod));
		}

		@Override
		protected boolean isCompat() {
			return true;
		}
	}

}
