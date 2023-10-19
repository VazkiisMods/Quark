package vazkii.quark.content.automation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EndRodBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import vazkii.quark.api.ICollateralMover;
import vazkii.quark.base.Quark;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.handler.CreativeTabHandler;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.automation.module.IronRodModule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

public class IronRodBlock extends EndRodBlock implements ICollateralMover, IQuarkBlock {

	private final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

	public IronRodBlock(QuarkModule module) {
		super(Block.Properties.of(Material.METAL, DyeColor.GRAY)
				.strength(5F, 10F)
				.sound(SoundType.METAL)
				.noOcclusion());

		Quark.ZETA.registry.registerBlock(this, "iron_rod", true);
		CreativeTabHandler.addTab(this, CreativeModeTab.TAB_DECORATIONS);

		RenderLayerHandler.setRenderType(this, RenderLayerHandler.RenderTypeSkeleton.CUTOUT);

		this.module = module;
	}

	@Nullable
	@Override
	public QuarkModule getModule() {
		return module;
	}

	@Override
	public IronRodBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Override
	protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(CONNECTED);
	}

	@Override
	public boolean isCollateralMover(Level world, BlockPos source, Direction moveDirection, BlockPos pos) {
		return moveDirection == world.getBlockState(pos).getValue(FACING) &&
			!world.getBlockState(pos.relative(moveDirection)).is(IronRodModule.ironRodImmuneTag);
	}

	@Override
	public MoveResult getCollateralMovement(Level world, BlockPos source, Direction moveDirection, Direction side, BlockPos pos) {
		return side == moveDirection && !world.getBlockState(pos.relative(side)).is(IronRodModule.ironRodImmuneTag) ? MoveResult.BREAK : MoveResult.SKIP;
	}

	@Override
	public void animateTick(@Nonnull BlockState stateIn, @Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull RandomSource rand) {
		// NO-OP
	}


}
