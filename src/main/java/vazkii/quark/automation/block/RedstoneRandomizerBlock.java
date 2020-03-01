package vazkii.quark.automation.block;

import java.util.EnumSet;
import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import vazkii.quark.automation.base.RandomizerPowerState;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.Module;

/**
 * @author WireSegal
 * Created at 9:57 AM on 8/26/19.
 */

public class RedstoneRandomizerBlock extends QuarkBlock {

    protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<RandomizerPowerState> POWERED = EnumProperty.create("powered", RandomizerPowerState.class);

    public RedstoneRandomizerBlock(String regname, Module module, ItemGroup creativeTab, Properties properties) {
        super(regname, module, creativeTab, properties);

        setDefaultState(getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(POWERED, RandomizerPowerState.OFF));
        
		RenderLayerHandler.setRenderType(this, RenderTypeSkeleton.CUTOUT);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        boolean isPowered = isPowered(state);
        boolean willBePowered = shouldBePowered(world, pos, state);
        if(isPowered != willBePowered) {
            if (!willBePowered)
                state = state.with(POWERED, RandomizerPowerState.OFF);
            else
                state = state.with(POWERED, rand.nextBoolean() ? RandomizerPowerState.LEFT : RandomizerPowerState.RIGHT);

            world.setBlockState(pos, state);
        }
    }

    protected void updateState(World world, BlockPos pos, BlockState state) {
        boolean isPowered = isPowered(state);
        boolean willBePowered = shouldBePowered(world, pos, state);
        if (isPowered != willBePowered && !world.getPendingBlockTicks().isTickPending(pos, this)) {
            TickPriority priority = isPowered ? TickPriority.VERY_HIGH : TickPriority.HIGH;

            world.getPendingBlockTicks().scheduleTick(pos, this, 2, priority);
        }

    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
        return hasSolidSideOnTop(world, pos.down());
    }

    protected boolean isPowered(BlockState state) {
        return state.get(POWERED) != RandomizerPowerState.OFF;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return blockState.getWeakPower(blockAccess, pos, side);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        RandomizerPowerState powerState = blockState.get(POWERED);
        switch (powerState) {
            case RIGHT:
                return blockState.get(FACING).rotateY() == side ? 15 : 0;
            case LEFT:
                return blockState.get(FACING).rotateYCCW() == side ? 15 : 0;
            default:
                return 0;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (state.isValidPosition(world, pos))
            this.updateState(world, pos, state);
        else
            breakAndDrop(this, state, world, pos);
    }

    public static void breakAndDrop(Block block, BlockState state, World world, BlockPos pos) {
        spawnDrops(state, world, pos, null);
        world.removeBlock(pos, false);

        for(Direction direction : Direction.values())
            world.notifyNeighborsOfStateChange(pos.offset(direction), block);
    }

    protected boolean shouldBePowered(World world, BlockPos pos, BlockState state) {
        return this.calculateInputStrength(world, pos, state) > 0;
    }

    protected int calculateInputStrength(World world, BlockPos pos, BlockState state) {
        Direction face = state.get(FACING);
        BlockPos checkPos = pos.offset(face);
        int strength = world.getRedstonePower(checkPos, face);
        if (strength >= 15) {
            return strength;
        } else {
            BlockState checkState = world.getBlockState(checkPos);
            return Math.max(strength, checkState.getBlock() == Blocks.REDSTONE_WIRE ? checkState.get(RedstoneWireBlock.POWER) : 0);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canProvidePower(BlockState state) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (this.shouldBePowered(world, pos, state)) {
            world.getPendingBlockTicks().scheduleTick(pos, this, 1);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
        notifyNeighbors(this, world, pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onReplaced(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        if (!isMoving && state.getBlock() != newState.getBlock()) {
            super.onReplaced(state, world, pos, newState, false);
            notifyNeighbors(this, world, pos, state);
        }
    }

    public static void notifyNeighbors(Block block, World world, BlockPos pos, BlockState state) {
        Direction face = state.get(FACING);
        BlockPos neighborPos = pos.offset(face.getOpposite());
        if (ForgeEventFactory.onNeighborNotify(world, pos, world.getBlockState(pos), EnumSet.of(face.getOpposite()), false).isCanceled())
            return;
        world.neighborChanged(neighborPos, block, pos);
        world.notifyNeighborsOfStateExcept(neighborPos, block, face);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.get(POWERED) != RandomizerPowerState.OFF) {
            double x = (double)((float)pos.getX() + 0.5F) + (double)(rand.nextFloat() - 0.5F) * 0.2D;
            double y = (double)((float)pos.getY() + 0.4F) + (double)(rand.nextFloat() - 0.5F) * 0.2D;
            double z = (double)((float)pos.getZ() + 0.5F) + (double)(rand.nextFloat() - 0.5F) * 0.2D;

            worldIn.addParticle(RedstoneParticleData.REDSTONE_DUST, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

}
