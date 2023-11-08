package org.violetmoon.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.violetmoon.quark.base.block.IQuarkBlock;
import org.violetmoon.quark.base.handler.MiscUtil;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.RenderLayerRegistry;

public class HollowLogBlock extends HollowPillarBlock {

    private final boolean flammable;

    public HollowLogBlock(Block sourceLog, ZetaModule module, boolean flammable) {
        this(IQuarkBlock.inherit(sourceLog, "hollow_%s"), sourceLog, module, flammable);
    }

    public HollowLogBlock(String name, Block sourceLog, ZetaModule module, boolean flammable) {
        super(name, module, CreativeModeTab.TAB_DECORATIONS,
                MiscUtil.copyPropertySafe(sourceLog)
                        .isSuffocating((s, g, p) -> false));

        this.flammable = flammable;
        module.zeta.renderLayerRegistry.put(this, RenderLayerRegistry.Layer.CUTOUT_MIPPED);
    }


    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return flammable;
    }
}

