package vazkii.quark.content.building.block;

import java.util.function.BooleanSupplier;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import org.jetbrains.annotations.Nullable;
import vazkii.quark.base.Quark;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.handler.CreativeTabHandler;
import vazkii.zeta.module.ZetaModule;
import vazkii.quark.content.building.module.HedgesModule;
import vazkii.quark.content.world.block.BlossomLeavesBlock;
import vazkii.zeta.registry.IZetaBlockColorProvider;
import vazkii.zeta.registry.RenderLayerRegistry;

//TODO ZETA: extend QuarkFenceBlock
public class HedgeBlock extends FenceBlock implements IQuarkBlock, IZetaBlockColorProvider {
	
	private static final VoxelShape WOOD_SHAPE = box(6F, 0F, 6F, 10F, 15F, 10F);
	private static final VoxelShape HEDGE_CENTER_SHAPE = box(2F, 1F, 2F, 14F, 16F, 14F);
	private static final VoxelShape NORTH_SHAPE = box(2F, 1F, 0F, 14F, 16F, 2F);
	private static final VoxelShape SOUTH_SHAPE = box(2F, 1F, 14F, 14F, 16F, 15F);
	private static final VoxelShape EAST_SHAPE = box(14F, 1F, 2F, 16F, 16F, 14F);
	private static final VoxelShape WEST_SHAPE = box(0F, 1F, 2F, 2F, 16F, 14F);
	private static final VoxelShape EXTEND_SHAPE = box(2F, 0F, 2F, 14F, 1F, 14F);
	
	private final Object2IntMap<BlockState> hedgeStateToIndex;
	private final VoxelShape[] hedgeShapes;

	private final ZetaModule module;
	public final BlockState leafState;
	private BooleanSupplier enabledSupplier = () -> true;

	public static final BooleanProperty EXTEND = BooleanProperty.create("extend");

	public HedgeBlock(ZetaModule module, Block fence, Block leaf) {
		super(Block.Properties.copy(fence));

		this.module = module;
		this.leafState = leaf.defaultBlockState();

		ResourceLocation leafRes = Quark.ZETA.registry.getRegistryName(leaf, Registry.BLOCK);
		if (leaf instanceof BlossomLeavesBlock) {
			String colorName = leafRes.getPath().replaceAll("_blossom_leaves", "");
			Quark.ZETA.registry.registerBlock(this, colorName + "_blossom_hedge", true);
		} else {
			String resloc = leafRes.getPath().replaceAll("_leaves", "_hedge");
			Quark.ZETA.registry.registerBlock(this, resloc, true);
		}
		CreativeTabHandler.addTab(this, CreativeModeTab.TAB_DECORATIONS);

		module.zeta.renderLayerRegistry.put(this, RenderLayerRegistry.Layer.CUTOUT);

		registerDefaultState(defaultBlockState().setValue(EXTEND, false));
		
		hedgeStateToIndex = new Object2IntOpenHashMap<>();
		hedgeShapes = cacheHedgeShapes(stateDefinition.getPossibleStates());
	}

	public BlockState getLeaf() {
		return leafState;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return hedgeShapes[getHedgeAABBIndex(state)];
	}	
	
	private VoxelShape[] cacheHedgeShapes(ImmutableList<BlockState> possibleStates) {
		VoxelShape[] shapes = new VoxelShape[possibleStates.size()];
		
		for(int i = 0; i < shapes.length; i++) {
			BlockState state = possibleStates.get(i);
			int realIndex = getHedgeAABBIndex(state);

			VoxelShape finishedShape = Shapes.or(state.getValue(HedgeBlock.EXTEND) ? EXTEND_SHAPE : WOOD_SHAPE, HEDGE_CENTER_SHAPE);
			if(state.getValue(FenceBlock.NORTH))
				finishedShape = Shapes.or(finishedShape, NORTH_SHAPE);
			if(state.getValue(FenceBlock.SOUTH))
				finishedShape = Shapes.or(finishedShape, SOUTH_SHAPE);
			if(state.getValue(FenceBlock.EAST))
				finishedShape = Shapes.or(finishedShape, EAST_SHAPE);
			if(state.getValue(FenceBlock.WEST))
				finishedShape = Shapes.or(finishedShape, WEST_SHAPE);
			
			shapes[realIndex] = finishedShape;
		}
		
		return shapes;
	}

	protected int getHedgeAABBIndex(BlockState curr) {
		return hedgeStateToIndex.computeIntIfAbsent(curr, (state) -> {
			int i = 0;

			if(state.getValue(FenceBlock.NORTH))
				i |= 0b00001;
			if(state.getValue(FenceBlock.SOUTH))
				i |= 0b00010;
			if(state.getValue(FenceBlock.EAST))
				i |= 0b00100;
			if(state.getValue(FenceBlock.WEST))
				i |= 0b01000;
			if(state.getValue(EXTEND))
				i |= 0b10000;

			return i;
		});
	}

	@Override
	public boolean connectsTo(BlockState state, boolean isSideSolid, @Nonnull Direction direction) {
		return state.is(HedgesModule.hedgesTag);
	}

	@Override
	public boolean canSustainPlantZeta(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull Direction facing, @Nonnull String plantabletype) {
		return facing == Direction.UP && !state.getValue(WATERLOGGED) && "plains".equals(plantabletype);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockGetter iblockreader = context.getLevel();
		BlockPos blockpos = context.getClickedPos();
		BlockPos down = blockpos.below();
		BlockState downState = iblockreader.getBlockState(down);

		return super.getStateForPlacement(context)
				.setValue(EXTEND, downState.getBlock() instanceof HedgeBlock);
	}

	@Nonnull
	@Override
	public BlockState updateShape(BlockState stateIn, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull LevelAccessor worldIn, @Nonnull BlockPos currentPos, @Nonnull BlockPos facingPos) {
		if (stateIn.getValue(WATERLOGGED)) {
			worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
		}

		if(facing == Direction.DOWN)
			return stateIn.setValue(EXTEND, facingState.getBlock() instanceof HedgeBlock);

		return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(EXTEND);
	}

	@Override
	public @Nullable String getBlockColorProviderName() {
		return "hedge";
	}

	@Override
	public @Nullable String getItemColorProviderName() {
		return "hedge";
	}

	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Override
	public ZetaModule getModule() {
		return module;
	}

	@Override
	public HedgeBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

}
