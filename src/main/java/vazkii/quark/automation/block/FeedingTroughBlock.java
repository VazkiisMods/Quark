package vazkii.quark.automation.block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import vazkii.quark.automation.tile.FeedingTroughTileEntity;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.Module;

/**
 * @author WireSegal
 * Created at 9:39 AM on 9/20/19.
 */
public class FeedingTroughBlock extends QuarkBlock {

    private static final SoundType WOOD_WITH_PLANT_STEP = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_WOOD_BREAK, SoundEvents.BLOCK_GRASS_STEP, SoundEvents.BLOCK_WOOD_PLACE, SoundEvents.BLOCK_WOOD_HIT, SoundEvents.BLOCK_WOOD_FALL);

    public static BooleanProperty FULL = BooleanProperty.create("full");

    public static final VoxelShape CUBOID_SHAPE = makeCuboidShape(0, 0, 0, 16, 8, 16);
    public static final VoxelShape EMPTY_SHAPE = VoxelShapes.combineAndSimplify(CUBOID_SHAPE,
            makeCuboidShape(2, 2, 2, 14, 8, 14), IBooleanFunction.ONLY_FIRST);

    public static final VoxelShape FULL_SHAPE = VoxelShapes.combineAndSimplify(CUBOID_SHAPE,
            makeCuboidShape(2, 6, 2, 14, 8, 14), IBooleanFunction.ONLY_FIRST);


    public FeedingTroughBlock(String regname, Module module, ItemGroup creativeTab, Properties properties) {
        super(regname, module, creativeTab, properties);
        setDefaultState(getDefaultState().with(FULL, false));
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, ISelectionContext context) {
        return EMPTY_SHAPE;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getRaytraceShape(BlockState state, IBlockReader world, BlockPos pos) {
        return CUBOID_SHAPE;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return state.get(FULL) ? FULL_SHAPE : EMPTY_SHAPE;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FULL);
    }

    @Override
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
        if (state.get(FULL))
            return WOOD_WITH_PLANT_STEP;
        return super.getSoundType(state, world, pos, entity);
    }

    @Override
    public void onFallenUpon(World world, BlockPos pos, Entity entity, float distance) {
        if (world.getBlockState(pos).get(FULL))
            entity.onLivingFall(distance, 0.2F);
        else
            super.onFallenUpon(world, pos, entity, distance);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onReplaced(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof FeedingTroughTileEntity) {
                InventoryHelper.dropInventoryItems(world, pos, (FeedingTroughTileEntity)tile);
                world.updateComparatorOutputLevel(pos, this);
            }

            super.onReplaced(state, world, pos, newState, isMoving);
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new FeedingTroughTileEntity();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
        return Container.calcRedstone(world.getTileEntity(pos));
    }

    
    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
        if (world.isRemote)
            return ActionResultType.SUCCESS;
        else {
            INamedContainerProvider container = this.getContainer(state, world, pos);
            if (container != null)
                player.openContainer(container);

            return ActionResultType.SUCCESS;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean eventReceived(BlockState state, World world, BlockPos pos, int id, int param) {
        super.eventReceived(state, world, pos, id, param);
        TileEntity tile = world.getTileEntity(pos);
        return tile != null && tile.receiveClientEvent(id, param);
    }

    @Override
    @Nullable
    @SuppressWarnings("deprecation")
    public INamedContainerProvider getContainer(BlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof INamedContainerProvider ? (INamedContainerProvider)tile : null;
    }

}
