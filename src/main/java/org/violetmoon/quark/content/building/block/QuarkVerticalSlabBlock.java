package org.violetmoon.quark.content.building.block;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.block.IQuarkBlock;
import org.violetmoon.quark.base.block.QuarkSlabBlock;
import org.violetmoon.quark.base.client.handler.RequiredModTooltipHandler;
import org.violetmoon.quark.base.handler.CreativeTabHandler;
import org.violetmoon.zeta.module.ZetaModule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

public class QuarkVerticalSlabBlock extends VerticalSlabBlock implements IQuarkBlock {

    private final ZetaModule module;
    private BooleanSupplier enabledSupplier = () -> true;

    public QuarkVerticalSlabBlock(Block parent, ZetaModule module) {
        super(() -> parent, Block.Properties.copy(parent));
	    String resloc = IQuarkBlock.inherit(parent, s -> s.replace("_slab", "_vertical_slab"));
	    Quark.ZETA.registry.registerBlock(this, resloc, true);

	    this.module = module;

        CreativeTabHandler.addTab(this, CreativeModeTab.TAB_BUILDING_BLOCKS);

        if (module.category.isAddon())
            RequiredModTooltipHandler.map(this, module.category.requiredMod);


        if (!(parent instanceof SlabBlock))
            throw new IllegalArgumentException("Can't rotate a non-slab block into a vertical slab.");

        if (parent instanceof QuarkSlabBlock quarkSlab)
            setCondition(quarkSlab.parent::isEnabled);
    }


    @Override
    public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
        if (isEnabled() || group == CreativeModeTab.TAB_SEARCH)
            super.fillItemCategory(group, items);
    }

    @Override
    public QuarkVerticalSlabBlock setCondition(BooleanSupplier enabledSupplier) {
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

}
