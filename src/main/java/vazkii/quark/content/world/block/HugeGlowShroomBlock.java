package vazkii.quark.content.world.block;

import java.util.function.BooleanSupplier;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.Quark;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.handler.CreativeTabHandler;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.zeta.module.ZetaModule;
import vazkii.quark.content.world.module.GlimmeringWealdModule;

public class HugeGlowShroomBlock extends HugeMushroomBlock implements IQuarkBlock {

	private final ZetaModule module;
	private final boolean glowing;

	public HugeGlowShroomBlock(String name, ZetaModule module, final boolean glowing) {
		super(Block.Properties.copy(Blocks.RED_MUSHROOM_BLOCK)
				.lightLevel(b -> glowing ? 12 : 0)
				.hasPostProcess((a,b,c)-> glowing).emissiveRendering((a,b,c)-> glowing)
				.randomTicks()
				.noOcclusion());

		this.module = module;
		this.glowing = glowing;

		Quark.ZETA.registry.registerBlock(this, name, true);
		CreativeTabHandler.addTab(this, CreativeModeTab.TAB_DECORATIONS);
	}

	@Override
	public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return false;
	}

	@Override
	public int getFlammabilityZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 0;
	}

	@Override
	public int getFireSpreadSpeedZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 0;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(@Nonnull BlockState stateIn, @Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull RandomSource rand) {
		super.animateTick(stateIn, worldIn, pos, rand);

		BlockState below = worldIn.getBlockState(pos.below());
		if(glowing && rand.nextInt(10) == 0 && (below.isAir() || below.getBlock() == GlimmeringWealdModule.glow_shroom_ring))
			worldIn.addParticle(ParticleTypes.END_ROD, pos.getX() + rand.nextDouble(), pos.getY(), pos.getZ() + rand.nextDouble(), 0, -0.05 - Math.random() * 0.05, 0);
	}

	public static boolean place(LevelAccessor worldIn, RandomSource rand, BlockPos pos) {
		Block block = worldIn.getBlockState(pos.below()).getBlock();
		if (block != Blocks.DEEPSLATE) {
			return false;
		} else {
			BlockPos placePos = pos;

			BlockState stem = GlimmeringWealdModule.glow_shroom_stem.defaultBlockState();
			BlockState ring = GlimmeringWealdModule.glow_shroom_ring.defaultBlockState();
			BlockState cap = GlimmeringWealdModule.glow_shroom_block.defaultBlockState().setValue(HugeGlowShroomBlock.DOWN, false);

			int stemHeight1 = 2;
			int stemHeight2 = rand.nextInt(4);
			boolean hasBigCap = rand.nextDouble() < 0.6;

			// Check if it has space
			int totalHeight = stemHeight1 + stemHeight2 + (hasBigCap ? 2 : 1);
			int horizCheck = 2;

			for(int i = -horizCheck; i < horizCheck + 1; i++)
				for(int j = -horizCheck; j < horizCheck + 1; j++)
					for(int k = 1; k < totalHeight; k++) // start at 1 cuz ground layer doesn't matter
						if(!worldIn.getBlockState(placePos.offset(i, k, j)).isAir())
							return false;

			// Stem #1
			for(int i = 0; i < stemHeight1; i++) {
				worldIn.setBlock(placePos, stem, 2);
				placePos = placePos.above();
			}

			// Offset stem in random direction
			if(stemHeight2 > 0) {
				Direction dir = MiscUtil.HORIZONTALS[rand.nextInt(MiscUtil.HORIZONTALS.length)];
				placePos = placePos.relative(dir);
			}

			// Stem #2
			for(int i = 0; i < stemHeight2; i++) {
				worldIn.setBlock(placePos, stem, 2);
				placePos = placePos.above();
			}

			// Place rings on top of stem
			int ringHeight = Math.min(2, stemHeight2);
			for(int i = 0; i < ringHeight; i++) {
				for(Direction ringDir : MiscUtil.HORIZONTALS)
					worldIn.setBlock(placePos.relative(ringDir).relative(Direction.DOWN, i + 1), ring.setValue(GlowShroomRingBlock.FACING, ringDir), 2);
			}

			// Cap
			for(int i = -1; i < 2; i++)
				for(int j = -1; j < 2; j++)
					worldIn.setBlock(placePos.offset(i, 0, j), cap, 2);

			// Triangle cap
			if(hasBigCap)
				worldIn.setBlock(placePos.above(), cap, 2);

			return true;
		}
	}

	@Override
	public ZetaModule getModule() {
		return module;
	}

	@Override
	public IQuarkBlock setCondition(BooleanSupplier condition) {
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return true;
	}

}
