package vazkii.quark.content.automation.block;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.zeta.module.ZetaModule;
import vazkii.quark.content.automation.block.be.ChuteBlockEntity;
import vazkii.zeta.registry.RenderLayerRegistry;

public class ChuteBlock extends QuarkBlock implements EntityBlock {

	private static final VoxelShape INPUT_SHAPE = Block.box(0.0D, 10.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	private static final VoxelShape MIDDLE_SHAPE = Block.box(4.0D, 4.0D, 4.0D, 12.0D, 10.0D, 12.0D);
	private static final VoxelShape CHUTE_TOP_SHAPE = Shapes.or(MIDDLE_SHAPE, INPUT_SHAPE);
	private static final VoxelShape DOWN_SHAPE = Shapes.or(CHUTE_TOP_SHAPE, Block.box(6.0D, 0.0D, 6.0D, 10.0D, 4.0D, 10.0D));

	public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

	public ChuteBlock(String regname, ZetaModule module, CreativeModeTab creativeTab, Properties properties) {
		super(regname, module, creativeTab, properties);
		registerDefaultState(defaultBlockState().setValue(ENABLED, true));

		module.zeta.renderLayerRegistry.put(this, RenderLayerRegistry.Layer.CUTOUT_MIPPED);
	}

	@Override
	public int getFlammabilityZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 20;
	}

	@Override
	public int getFireSpreadSpeedZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 5;
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving) {
		boolean flag = !worldIn.hasNeighborSignal(pos);

		if(flag != state.getValue(ENABLED))
			worldIn.setBlock(pos, state.setValue(ENABLED, flag), 2 | 4);
	}

	@Nonnull
	@Override
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
		return DOWN_SHAPE;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ENABLED);
	}

	@Override
	public boolean isPathfindable(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull PathComputationType type) {
		return false;
	}

	@Override
	public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
		return new ChuteBlockEntity(pos, state);
	}

}
