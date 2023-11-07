package vazkii.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.zeta.module.ZetaModule;
import vazkii.zeta.registry.RenderLayerRegistry;

import javax.annotation.Nonnull;

import static vazkii.quark.base.handler.MiscUtil.directionProperty;

/**
 * Unfortunately, due to Ladder Weirdness (tm) this block is NYI
 */
public class HollowWoodBlock extends HollowFrameBlock {

    protected static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    protected static final BooleanProperty UP = BlockStateProperties.UP;
    protected static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    protected static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    protected static final BooleanProperty WEST = BlockStateProperties.WEST;
    protected static final BooleanProperty EAST = BlockStateProperties.EAST;

    private final boolean flammable;

    public HollowWoodBlock(Block sourceLog, ZetaModule module, boolean flammable) {
        this(IQuarkBlock.inherit(sourceLog, "hollow_%s"), sourceLog, module, flammable);
    }

    public HollowWoodBlock(String name, Block sourceLog, ZetaModule module, boolean flammable) {
        super(name, module, CreativeModeTab.TAB_DECORATIONS,
                MiscUtil.copyPropertySafe(sourceLog)
                        .isSuffocating((s, g, p) -> false));

        this.flammable = flammable;
        module.zeta.renderLayerRegistry.put(this, RenderLayerRegistry.Layer.CUTOUT_MIPPED);
        registerDefaultState(defaultBlockState()
            .setValue(DOWN, true)
            .setValue(UP, true)
            .setValue(NORTH, true)
            .setValue(SOUTH, true)
            .setValue(WEST, true)
            .setValue(EAST, true));
    }

    @Override
    public byte getShapeCode(BlockState state) {
        return shapeCode(state, DOWN, UP, NORTH, SOUTH, WEST, EAST);
    }

    @SuppressWarnings("deprecation") //Don't need the Forge extension
    @Override
    public BlockState rotate(BlockState state, Rotation direction) {
        BlockState newState = state;
        for (Direction dir : Direction.values())
            newState = newState.setValue(directionProperty(dir), state.getValue(directionProperty(direction.rotate(dir))));
        return newState;
    }

    @Nonnull
    @Override
    public BlockState mirror(@Nonnull BlockState state, @Nonnull Mirror mirror) {
        BlockState newState = state;
        for (Direction dir : Direction.values())
            newState = newState.setValue(directionProperty(dir), state.getValue(directionProperty(mirror.mirror(dir))));
        return newState;
    }

    // Temporary method
    @Override
    public @Nullable BlockState getToolModifiedStateZeta(BlockState state, UseOnContext context, String toolActionType, boolean simulate) {
        if ("axe_strip".equals(toolActionType)) {
            Vec3 exactPos = context.getClickLocation();
            BlockPos centerPos = context.getClickedPos();
            Direction face = Direction.getNearest(exactPos.x - (centerPos.getX() + 0.5), exactPos.y - (centerPos.getY() + 0.5), exactPos.z - (centerPos.getZ() + 0.5));
            return state.cycle(directionProperty(face));
        }
        return super.getToolModifiedStateZeta(state, context, toolActionType, simulate);
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> def) {
        super.createBlockStateDefinition(def);
        def.add(UP, DOWN, NORTH, SOUTH, WEST, EAST);
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return flammable;
    }
}

