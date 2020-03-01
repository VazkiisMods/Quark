package vazkii.quark.automation.block;

import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneDiodeBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.Module;

/**
 * @author WireSegal
 * Created at 10:37 AM on 8/26/19.
 */
public class RedstoneInductorBlock extends QuarkBlock implements IBlockColorProvider {
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;
    public static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;

    public RedstoneInductorBlock(String regname, Module module, ItemGroup creativeTab, Properties properties) {
        super(regname, module, creativeTab, properties);

        setDefaultState(getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(LOCKED, false)
                .with(POWER, 0));
        
		RenderLayerHandler.setRenderType(this, RenderTypeSkeleton.CUTOUT);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        if (!isLocked(world, pos, state)) {
            int currentPower = state.get(POWER);
            int power = this.calculateInputStrength(world, pos, state);
            if (currentPower != power)
                world.setBlockState(pos, state.with(POWER, power));
        }
    }

    protected void updateState(World world, BlockPos pos, BlockState state) {
        if (!isLocked(world, pos, state)) {
            int currentPower = state.get(POWER);
            int power = this.calculateInputStrength(world, pos, state);
            if (currentPower != power && !world.getPendingBlockTicks().isTickPending(pos, this)) {
                TickPriority priority = power > 0 ? TickPriority.VERY_HIGH : TickPriority.HIGH;
                world.getPendingBlockTicks().scheduleTick(pos, this, 1, priority);
            }
        }
    }

    protected boolean isLocked(IWorldReader world, BlockPos pos, BlockState state) {
        return getPowerOnSides(world, pos, state) > 0;
    }

    protected int getPowerOnSides(IWorldReader worldIn, BlockPos pos, BlockState state) {
        Direction direction = state.get(FACING);
        Direction direction1 = direction.rotateY();
        Direction direction2 = direction.rotateYCCW();
        return Math.max(this.getPowerOnSide(worldIn, pos.offset(direction1), direction1), this.getPowerOnSide(worldIn, pos.offset(direction2), direction2));
    }

    protected int getPowerOnSide(IWorldReader worldIn, BlockPos pos, Direction side) {
        BlockState state = worldIn.getBlockState(pos);
        return RedstoneDiodeBlock.isDiode(state) ? worldIn.getStrongPower(pos, side) : 0;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, LOCKED, POWER);
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

    @Override
    @SuppressWarnings("deprecation")
    public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return blockState.getWeakPower(blockAccess, pos, side);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return blockState.get(FACING) == side ? blockState.get(POWER) : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (state.isValidPosition(world, pos))
            this.updateState(world, pos, state);
        else
            RedstoneRandomizerBlock.breakAndDrop(this, state, world, pos);
    }

    protected int calculateInputStrength(World world, BlockPos pos, BlockState state) {
        Direction face = state.get(FACING);
        return Math.min(15, calculateInputStrength(world, pos, face) + calculateInputStrength(world, pos, face.rotateYCCW()) + calculateInputStrength(world, pos, face.rotateY()));
    }

    protected int calculateInputStrength(World world, BlockPos pos, Direction face) {
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
        BlockState state = this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
        return state.with(LOCKED, this.isLocked(context.getWorld(), context.getPos(), state));
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public BlockState updatePostPlacement(@Nonnull BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return !worldIn.isRemote() && facing.getAxis() != stateIn.get(FACING).getAxis() ? stateIn.with(LOCKED, this.isLocked(worldIn, currentPos, stateIn)) : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (calculateInputStrength(world, pos, state) > 0)
            world.getPendingBlockTicks().scheduleTick(pos, this, 1);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
        RedstoneRandomizerBlock.notifyNeighbors(this, world, pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onReplaced(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        if (!isMoving && state.getBlock() != newState.getBlock()) {
            super.onReplaced(state, world, pos, newState, false);
            RedstoneRandomizerBlock.notifyNeighbors(this, world, pos, state);
        }
    }

//    @Override does this work?
//    @SuppressWarnings("deprecation")
//    public boolean isSolid(BlockState state) {
//        return true;
//    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.get(POWER) != 0) {
            double x = (double)((float)pos.getX() + 0.5F) + (double)(rand.nextFloat() - 0.5F) * 0.2D;
            double y = (double)((float)pos.getY() + 0.4F) + (double)(rand.nextFloat() - 0.5F) * 0.2D;
            double z = (double)((float)pos.getZ() + 0.5F) + (double)(rand.nextFloat() - 0.5F) * 0.2D;
            float power = stateIn.get(POWER) / 15f;

            float r = power * 0.6F + 0.4F;
            float g = Math.max(0.0F, power * power * 0.7F - 0.5F);
            float b = Math.max(0.0F, power * power * 0.6F - 0.7F);
            worldIn.addParticle(new RedstoneParticleData(r, g, b, 1f), x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public IBlockColor getBlockColor() {
        return (state, world, pos, index) -> index == 1 ? RedstoneWireBlock.colorMultiplier(state.get(POWER)) : -1;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public IItemColor getItemColor() {
        return null;
    }
}
