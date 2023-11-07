package vazkii.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import vazkii.quark.base.block.QuarkGlassBlock;
import vazkii.zeta.module.ZetaModule;
import vazkii.quark.content.building.module.RainbowLampsModule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author WireSegal
 * Created at 12:31 PM on 9/19/19.
 */
public class RainbowLampBlock extends QuarkGlassBlock {

	public final float[] colorComponents;

	public static final BooleanProperty LIT = BlockStateProperties.LIT;

	private String corundumDescriptionId;

	public RainbowLampBlock(String regname, int color, ZetaModule module, MaterialColor materialColor) {
		super(regname, module, CreativeModeTab.TAB_REDSTONE, true,
				Properties.of(Material.GLASS, materialColor)
				.strength(0.3F, 0F)
				.sound(SoundType.AMETHYST)
				.lightLevel(b -> b.getValue(LIT) ? RainbowLampsModule.lightLevel : 0)
				.requiresCorrectToolForDrops()
				.noOcclusion());

		float r = ((color >> 16) & 0xff) / 255f;
		float g = ((color >> 8) & 0xff) / 255f;
		float b = (color & 0xff) / 255f;
		colorComponents = new float[]{r, g, b};
	}

	@Nonnull
	@Override
	public String getDescriptionId() {
		if (RainbowLampsModule.isCorundum()) {
			if (corundumDescriptionId == null) {
				corundumDescriptionId = super.getDescriptionId().replaceAll("crystal", "corundum");
			}
			return corundumDescriptionId;
		}

		return super.getDescriptionId();
	}


	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return this.defaultBlockState().setValue(LIT, ctx.getLevel().hasNeighborSignal(ctx.getClickedPos()));
	}

	@Override
	public void neighborChanged(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving) {
		if (!world.isClientSide) {
			boolean flag = state.getValue(LIT);
			if (flag != world.hasNeighborSignal(pos)) {
				if (flag) {
					world.scheduleTick(pos, this, 4);
				} else {
					world.setBlock(pos, state.cycle(LIT), 2);
				}
			}
		}
	}

	@Override
	public void tick(@Nonnull BlockState state, @Nonnull ServerLevel world, @Nonnull BlockPos pos, @Nonnull RandomSource rand) {
		if (state.getValue(LIT) && !world.hasNeighborSignal(pos)) {
			world.setBlock(pos, state.cycle(LIT), 2);
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(LIT);
	}

	@Nullable
	@Override
	public float[] getBeaconColorMultiplierZeta(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
		return state.getValue(LIT) ? colorComponents : null;
	}

}
