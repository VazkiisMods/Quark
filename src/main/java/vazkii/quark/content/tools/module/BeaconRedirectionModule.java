package vazkii.quark.content.tools.module;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity.BeaconBeamSection;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.world.block.CorundumClusterBlock;
import vazkii.quark.content.world.module.CorundumModule;

@LoadModule(category = ModuleCategory.TOOLS)
public class BeaconRedirectionModule extends QuarkModule {

	public static boolean staticEnabled;
	
	@Override
	public void configChanged() {
		staticEnabled = enabled;
	}
	
	// The value that comes out of this is fed onto a constant for the FOR loop that
	// computes the beacon segments, so we return 0 to run that code, or MAX_VALUE to not
	public static int tickBeacon(BeaconBlockEntity beacon) {
		if(!staticEnabled)
			return 0; 

		Level world = beacon.getLevel();
		BlockPos beaconPos = beacon.getBlockPos();
		BlockPos currPos = beaconPos;

		int horizontalMoves = 64;
		int targetHeight = world.getHeight(Heightmap.Types.WORLD_SURFACE, beaconPos.getX(), beaconPos.getZ());

		beacon.checkingBeamSections.clear();
		boolean broke = false;
		
		float[] currColor = new float[] { 1, 1, 1 };
		ExtendedBeamSegment currSegment = new ExtendedBeamSegment(Direction.UP, Vec3i.ZERO, currColor);

		List<BlockPos> seenPositions = new LinkedList<>();
		boolean check = true;
		boolean setColor = false;
		
		while(world.isInWorldBounds(currPos) && horizontalMoves > 0) {
			currPos = currPos.relative(currSegment.dir);
			if(currSegment.dir.getAxis().isHorizontal())
				horizontalMoves--;

			BlockState blockstate = world.getBlockState(currPos);
			Block block = blockstate.getBlock();
			float[] targetColor = blockstate.getBeaconColorMultiplier(world, currPos, beaconPos);

			if(isValidBlock(block)) {
				Direction dir = blockstate.getValue(BlockStateProperties.FACING);
				if(dir == currSegment.dir)
					currSegment.increaseHeight();
				else {
					check = true;
					beacon.checkingBeamSections.add(currSegment);
					
					targetColor = getTargetColor(block);
					if(targetColor[0] == 1F && targetColor[1] == 1F && targetColor[2] == 1F)
						targetColor = currColor;
					
					float[] mixedColor = new float[]{(currColor[0] + targetColor[0] * 3) / 4.0F, (currColor[1] + targetColor[1] * 3) / 4.0F, (currColor[2] + targetColor[2] * 3) / 4.0F};
					currColor = mixedColor;
					currSegment = new ExtendedBeamSegment(dir, currPos.subtract(beaconPos), currColor);
				}
			} else if(targetColor != null) {
				if(Arrays.equals(targetColor, currColor))
					currSegment.increaseHeight();
				else {
					check = true;
					beacon.checkingBeamSections.add(currSegment);

					float[] mixedColor = new float[]{(currColor[0] + targetColor[0]) / 2.0F, (currColor[1] + targetColor[1]) / 2.0F, (currColor[2] + targetColor[2]) / 2.0F};
					
					if(!setColor) {
						mixedColor = targetColor;
						setColor = true;
					}
					
					currColor = mixedColor;
					currSegment = new ExtendedBeamSegment(currSegment.dir, currPos.subtract(beaconPos), mixedColor);
				}
			} else {
				if (block == Blocks.BEDROCK)
					continue; //Bedrock blocks don't stop beacon beams
				else if (blockstate.getLightBlock(world, currPos) >= 15) {
					broke = true;
					break;
				}

				currSegment.increaseHeight();
			}
			
			if(check) {
				if(seenPositions.contains(currPos)) {
					broke = true;
					break;
				} else seenPositions.add(currPos);
			}
		}
		
		if(horizontalMoves == 0 || currPos.getY() <= 0)
			broke = true;

		if(!broke) {
			beacon.checkingBeamSections.add(currSegment);
			beacon.lastCheckY = targetHeight + 1;
		} else {
			beacon.checkingBeamSections.clear();
			beacon.lastCheckY = targetHeight;
		}
		

		return Integer.MAX_VALUE;
	}
	
	private static boolean isValidBlock(Block block) {
		return CorundumModule.staticEnabled ? block instanceof CorundumClusterBlock : block == Blocks.AMETHYST_CLUSTER;
	}
	
	private static float[] getTargetColor(Block block) {
		return block instanceof CorundumClusterBlock ? ((CorundumClusterBlock) block).base.colorComponents : new float[] { 1F, 1F, 1F };
	}

	public static class ExtendedBeamSegment extends BeaconBeamSection {

		public final Direction dir;
		public final Vec3i offset;
		
		private boolean isTurn = false;

		public ExtendedBeamSegment(Direction dir, Vec3i offset, float[] colorsIn) {
			super(colorsIn);
			this.offset = offset;
			this.dir = dir;
		}

		public void makeTurn() {
			isTurn = true;
		}
		
		public boolean isTurn() {
			return isTurn;
		}
		
		@Override
		public void increaseHeight() { // increase visibility
			super.increaseHeight();
		}

	}
	
}
