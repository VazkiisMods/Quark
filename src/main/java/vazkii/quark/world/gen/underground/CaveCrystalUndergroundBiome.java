package vazkii.quark.world.gen.underground;

import it.unimi.dsi.fastutil.ints.Int2ByteArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ByteMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import vazkii.quark.world.gen.UndergroundBiomeGenerator.Context;
import vazkii.quark.world.module.underground.CaveCrystalUndergroundBiomeModule;

import java.util.Random;

public class CaveCrystalUndergroundBiome extends BasicUndergroundBiome {

	public CaveCrystalUndergroundBiome() {
		super(Blocks.AIR.getDefaultState(), Blocks.STONE.getDefaultState(), Blocks.STONE.getDefaultState());
	}

	private static final Int2ByteMap CRYSTAL_DATA = new Int2ByteArrayMap();

	private final BlockState LAVA = Blocks.LAVA.getDefaultState();
	private final BlockState STONE = Blocks.STONE.getDefaultState();

	@Override
	public void fillCeiling(Context context, BlockPos pos, BlockState state) {
		byte raw = calculateRawColorData(context.source);
		int floorIdx = raw & 0xF;
		int ceilIdx = (raw >> 4) & 0xF;
		if (ceilIdx >= floorIdx)
			ceilIdx++;

		if(context.random.nextDouble() < CaveCrystalUndergroundBiomeModule.crystalSpawnChance) {
			BlockPos floorPos = pos.down();
			while (context.world.isAirBlock(floorPos))
				floorPos = floorPos.down();

			if (!context.world.getBlockState(pos).isIn(CaveCrystalUndergroundBiomeModule.crystalTag)) {
				int dist = pos.getY() - floorPos.getY();

				int start = 0;
				if (!STONE_TYPES_MATCHER.test(context.world.getBlockState(pos.up())))
					start++;

				BlockState crystalState = CaveCrystalUndergroundBiomeModule.crystal(ceilIdx).getDefaultState();

				for (int i = start; i <= dist * 3 / 4; i++)
					context.world.setBlockState(pos.offset(Direction.DOWN, i), crystalState, 2);
			}
		}
	}

	@Override
	public void fillFloor(Context context, BlockPos pos, BlockState state) {
		byte raw = calculateRawColorData(context.source);
		int floorIdx = raw & 0xF;

		if(context.random.nextDouble() < CaveCrystalUndergroundBiomeModule.crystalSpawnChance) {
			BlockPos ceilPos = pos.up();
			while (context.world.isAirBlock(ceilPos))
				ceilPos = ceilPos.up();

			if (!context.world.getBlockState(pos).isIn(CaveCrystalUndergroundBiomeModule.crystalTag)) {
				int dist = ceilPos.getY() - pos.getY();

				int start = 0;
				if (!STONE_TYPES_MATCHER.test(context.world.getBlockState(pos.down())))
					start++;

				BlockState crystalState = CaveCrystalUndergroundBiomeModule.crystal(floorIdx).getDefaultState();

				for (int i = start; i <= dist * 3 / 4; i++)
					context.world.setBlockState(pos.offset(Direction.UP, i), crystalState, 2);
				return;
			}
		}

		if (CaveCrystalUndergroundBiomeModule.crystalsGrowInLava) {
			context.world.setBlockState(pos, LAVA, 2);

			for (Direction dir : Direction.values()) {
				if (dir == Direction.UP)
					continue;

				BlockPos shiftPos = pos.offset(dir);

				if (!context.world.getBlockState(shiftPos).isSolidSide(context.world, shiftPos, dir.getOpposite()))
					context.world.setBlockState(shiftPos, STONE, 2);
			}
		}
	}

	private static byte calculateRawColorData(BlockPos source) {
		return CRYSTAL_DATA.computeIfAbsent(source.hashCode(), (src) -> {
			Random rand = new Random(src);
			return (byte) ((rand.nextInt(8) << 4) | rand.nextInt(9));
		});
	}

}
