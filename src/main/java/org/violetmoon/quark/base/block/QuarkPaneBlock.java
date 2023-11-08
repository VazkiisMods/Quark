package org.violetmoon.quark.base.block;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import org.violetmoon.quark.base.handler.CreativeTabHandler;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.RenderLayerRegistry.Layer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

public class QuarkPaneBlock extends IronBarsBlock implements IQuarkBlock {

	public final ZetaModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkPaneBlock(String name, ZetaModule module, Block.Properties properties, Layer renderLayer) {
		super(properties);

		this.module = module;
		module.zeta.registry.registerBlock(this, name, true);
		CreativeTabHandler.addTab(this, CreativeModeTab.TAB_DECORATIONS);

		if(renderLayer != null)
			module.zeta.renderLayerRegistry.put(this, renderLayer);
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
	public QuarkPaneBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}


}
