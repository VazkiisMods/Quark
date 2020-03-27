package vazkii.quark.oddities.block;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.Module;
import vazkii.quark.oddities.tile.MagnetizedBlockTileEntity;

/**
 * @author WireSegal
 * Created at 3:05 PM on 2/26/20.
 */
public class MovingMagnetizedBlock extends QuarkBlock {
    public static final DirectionProperty FACING = PistonHeadBlock.FACING;

    public MovingMagnetizedBlock(Module module) {
        super("magnetized_block", module, null, Block.Properties.create(Material.PISTON).hardnessAndResistance(-1.0F).variableOpacity().noDrops().notSolid());
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return null;
    }

    @Override
    public void onReplaced(BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            MagnetizedBlockTileEntity tile = getMagnetTileEntity(worldIn, pos);
            if (tile != null)
                tile.clearMagnetTileEntity();
        }
    }

    @Override
    public boolean isNormalCube(BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos) {
        return false;
    }

    @Override
    public boolean causesSuffocation(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos) {
        return false;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isRemote && worldIn.getTileEntity(pos) == null) {
            worldIn.removeBlock(pos, false);
            return ActionResultType.SUCCESS;
        } else
            return ActionResultType.PASS;
    }

    @Override
    @Nonnull
    public List<ItemStack> getDrops(@Nonnull BlockState state, @Nonnull LootContext.Builder builder) {
        MagnetizedBlockTileEntity tile = this.getMagnetTileEntity(builder.getWorld(), builder.assertPresent(LootParameters.POSITION));
        return tile == null ? Collections.emptyList() : tile.getMagnetState().getDrops(builder);
    }

    @Override
    @Nonnull
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

    @Override
    @Nonnull
    public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, ISelectionContext context) {
        MagnetizedBlockTileEntity tile = this.getMagnetTileEntity(worldIn, pos);
        return tile != null ? tile.getCollisionShape(worldIn, pos) : VoxelShapes.empty();
    }

    @Nullable
    private MagnetizedBlockTileEntity getMagnetTileEntity(IBlockReader world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof MagnetizedBlockTileEntity ? (MagnetizedBlockTileEntity)tile : null;
    }

    @Override
    @Nonnull
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public BlockState rotate(@Nonnull BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    @Nonnull
    public BlockState mirror(@Nonnull BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean allowsMovement(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, PathType type) {
        return false;
    }
}
