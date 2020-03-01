package vazkii.quark.automation.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import vazkii.quark.automation.tile.WeatherSensorTileEntity;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.Module;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author WireSegal
 * Created at 9:01 AM on 8/26/19.
 */
public class WeatherSensorBlock extends QuarkBlock {
    public static final IntegerProperty POWER = IntegerProperty.create("power", 0, 2);
    public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;
    public static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);

    public WeatherSensorBlock(String regname, Module module, ItemGroup creativeTab, Properties properties) {
        super(regname, module, creativeTab, properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(POWER, 0).with(INVERTED, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWER, INVERTED);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new WeatherSensorTileEntity();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canProvidePower(BlockState state) {
        return true;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext selection) {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isTransparent(BlockState p_220074_1_) { // blocksLight
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return (int) (state.get(POWER) * 7.5f);
    }

    public static void updatePower(BlockState state, World world, BlockPos pos) {
        if (world.dimension.hasSkyLight()) {
            boolean inverted = state.get(INVERTED);

            if (world.isThundering())
                world.setBlockState(pos, state.with(POWER, inverted ? 0 : 2));
            else if (world.isRaining() && world.getBiome(pos).getPrecipitation() != Biome.RainType.NONE)
                world.setBlockState(pos, state.with(POWER, 1));
            else
                world.setBlockState(pos, state.with(POWER, inverted ? 2 : 0));
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) {
        if (player.isAllowEdit()) {
            if (world.isRemote) {
                return ActionResultType.SUCCESS;
            } else {
                BlockState inverted = state.cycle(INVERTED);
                world.setBlockState(pos, inverted, 4);
                updatePower(inverted, world, pos);
                return ActionResultType.SUCCESS;
            }
        } else {
            return super.onBlockActivated(state, world, pos, player, hand, ray);
        }
    }
}
