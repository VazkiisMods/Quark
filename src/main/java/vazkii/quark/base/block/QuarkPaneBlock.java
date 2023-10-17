package vazkii.quark.base.block;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.CreativeTabHandler;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

public class QuarkPaneBlock extends IronBarsBlock implements IQuarkBlock {

	public final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkPaneBlock(String name, QuarkModule module, Block.Properties properties, RenderTypeSkeleton renderType) {
		super(properties);

		this.module = module;
		Quark.ZETA.registry.registerBlock(this, name, true);
		CreativeTabHandler.addTab(this, CreativeModeTab.TAB_DECORATIONS);

		if(renderType != null)
			RenderLayerHandler.setRenderType(this, renderType);
	}

	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Nullable
	@Override
	public QuarkModule getModule() {
		return module;
	}

	@Override
	public QuarkPaneBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}


}
