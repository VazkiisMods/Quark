package vazkii.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.module.QuarkModule;

public class HollowLogBlock extends HollowPillarBlock {

    private final boolean flammable;

    public HollowLogBlock(Block sourceLog, QuarkModule module, boolean flammable) {
        this(IQuarkBlock.inherit(sourceLog, "hollow_%s"), sourceLog, module, flammable);
    }

    public HollowLogBlock(String name, Block sourceLog, QuarkModule module, boolean flammable) {
        super(name, module, CreativeModeTab.TAB_DECORATIONS,
                MiscUtil.copyPropertySafe(sourceLog)
                        .isSuffocating((s, g, p) -> false));

        this.flammable = flammable;
        RenderLayerHandler.setRenderType(this, RenderLayerHandler.RenderTypeSkeleton.CUTOUT_MIPPED);
    }


    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return flammable;
    }
}

