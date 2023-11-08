package org.violetmoon.quark.base.block;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.client.handler.RequiredModTooltipHandler;
import org.violetmoon.quark.base.handler.CreativeTabHandler;
import org.violetmoon.zeta.module.ZetaModule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

public class QuarkBlock extends Block implements IQuarkBlock {

    private final ZetaModule module;
    private BooleanSupplier enabledSupplier = () -> true;

    public QuarkBlock(String regname, ZetaModule module, CreativeModeTab creativeTab, Properties properties) {
        super(properties);

        Quark.ZETA.registry.registerBlock(this, regname);
        this.module = module;

        CreativeTabHandler.addTab(this, creativeTab);

        if (module.category.isAddon())
            RequiredModTooltipHandler.map(this, module.category.requiredMod);
    }

    @Override
    public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
        if (isEnabled() || group == CreativeModeTab.TAB_SEARCH)
            super.fillItemCategory(group, items);
    }

    @Override
    public QuarkBlock setCondition(BooleanSupplier enabledSupplier) {
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

    @Nullable
    @SuppressWarnings("unchecked")
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> thisType, BlockEntityType<E> targetType, BlockEntityTicker<? super E> ticker) {
        return targetType == thisType ? (BlockEntityTicker<A>) ticker : null;
    }

    public interface Constructor<T extends Block> {

        T make(String regname, ZetaModule module, CreativeModeTab creativeTab, Properties properties);

    }

}
