package vazkii.quark.content.building.block;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import vazkii.quark.base.Quark;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.block.QuarkSlabBlock;
import vazkii.quark.base.client.handler.RequiredModTooltipHandler;
import vazkii.quark.base.handler.CreativeTabHandler;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

public class QuarkVerticalSlabBlock extends VerticalSlabBlock implements IQuarkBlock {

    private final QuarkModule module;
    private BooleanSupplier enabledSupplier = () -> true;

    public QuarkVerticalSlabBlock(Block parent, QuarkModule module) {
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
    public QuarkModule getModule() {
        return module;
    }

}
