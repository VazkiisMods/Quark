package vazkii.quark.content.tools.module;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity.BeaconBeamSection;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import vazkii.quark.base.handler.advancement.QuarkAdvancementHandler;
import vazkii.quark.base.handler.advancement.QuarkGenericTrigger;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.base.module.hint.HintManager;
import vazkii.quark.content.world.block.CorundumClusterBlock;
import vazkii.quark.content.world.module.CorundumModule;
import vazkii.zeta.event.ZConfigChanged;
import vazkii.zeta.event.ZGatherHints;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.bus.PlayEvent;

@LoadModule(category = "tools")
public class BeaconRedirectionModule extends QuarkModule {

	private static final TagKey<Block> BEACON_TRANSPARENT = BlockTags.create(new ResourceLocation("quark:beacon_transparent"));

	@Config
	public static int horizontalMoveLimit = 64;

	@Config(flag = "tinted_glass_dims")
	public static boolean allowTintedGlassTransparency = true;

	@Hint("tinted_glass_dims") Item tinted_glass = Items.TINTED_GLASS;

	public static boolean staticEnabled;

	public static QuarkGenericTrigger redirectTrigger;

	@LoadEvent
	public final void register(ZRegister event) {
		redirectTrigger = QuarkAdvancementHandler.registerGenericTrigger("redirect_beacon");
	}

	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
		staticEnabled = enabled;
	}

	@PlayEvent
	public void addAdditionalHints(ZGatherHints consumer) {
		final String redirectHint = "beacon_redirect_item";
		String type = "amethyst";

		if(!ModuleLoader.INSTANCE.isModuleEnabled(CorundumModule.class))
			HintManager.hintItem(consumer, Items.AMETHYST_CLUSTER, redirectHint);
		else
			type = "corundum";

		Component comp = Component.translatable("quark.jei.hint.beacon_redirection", Component.translatable("quark.jei.hint.beacon_" + type));
		consumer.accept(Items.BEACON, comp);
	}

	// The value that comes out of this is fed onto a constant for the FOR loop that
	// computes the beacon segments, so we return 0 to run that code, or MAX_VALUE to not
	public static int tickBeacon(BeaconBlockEntity beacon) {
		if(!staticEnabled)
			return 0;

		Level world = beacon.getLevel();
		BlockPos beaconPos = beacon.getBlockPos();
		BlockPos currPos = beaconPos;

		int horizontalMoves = horizontalMoveLimit;
		int targetHeight = world.getHeight(Heightmap.Types.WORLD_SURFACE, beaconPos.getX(), beaconPos.getZ());

		boolean broke = false;
		boolean didRedirection = false;

		beacon.checkingBeamSections.clear();

		float[] currColor = new float[] { 1, 1, 1 };
		float alpha = 1F;

		Direction lastDir = null;
		ExtendedBeamSegment currSegment = new ExtendedBeamSegment(Direction.UP, Vec3i.ZERO, currColor, alpha);

		Collection<BlockPos> seenPositions = new HashSet<>();
		boolean check = true;
		boolean hardColorSet = false;

		while(world.isInWorldBounds(currPos) && horizontalMoves > 0) {
			if(currSegment.dir == Direction.UP && currSegment.dir != lastDir) {
				int heightmapVal = world.getHeight(Heightmap.Types.WORLD_SURFACE, currPos.getX(), currPos.getZ());
				if(heightmapVal == (currPos.getY() + 1)) {
					currSegment.setHeight(heightmapVal + 1000);
					break;
				}


				lastDir = currSegment.dir;
			}

			currPos = currPos.relative(currSegment.dir);
			if(currSegment.dir.getAxis().isHorizontal())
				horizontalMoves--;
			else horizontalMoves = horizontalMoveLimit;

			BlockState blockstate = world.getBlockState(currPos);
			Block block = blockstate.getBlock();
			float[] targetColor = blockstate.getBeaconColorMultiplier(world, currPos, beaconPos);
			float targetAlpha = -1;

			if(allowTintedGlassTransparency) {
				if(block == Blocks.TINTED_GLASS)
					targetAlpha = (alpha < 0.3F ? 0F : (alpha / 2F));
			}

			if(isRedirectingBlock(block)) {
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
					alpha = 1F;
					didRedirection = true;
					lastDir = currSegment.dir;
					currSegment = new ExtendedBeamSegment(dir, currPos.subtract(beaconPos), currColor, alpha);
				}
			} else if(targetColor != null || targetAlpha != -1) {
				if(Arrays.equals(targetColor, currColor) && targetAlpha == alpha)
					currSegment.increaseHeight();
				else {
					check = true;
					beacon.checkingBeamSections.add(currSegment);

					float[] mixedColor = currColor;
					if(targetColor != null) {
						mixedColor = new float[]{(currColor[0] + targetColor[0]) / 2.0F, (currColor[1] + targetColor[1]) / 2.0F, (currColor[2] + targetColor[2]) / 2.0F};

						if(!hardColorSet) {
							mixedColor = targetColor;
							hardColorSet = true;
						}

						currColor = mixedColor;
					}

					if(targetAlpha != -1)
						alpha = targetAlpha;

					lastDir = currSegment.dir;
					currSegment = new ExtendedBeamSegment(currSegment.dir, currPos.subtract(beaconPos), mixedColor, alpha);
				}
			} else {
				boolean bedrock = blockstate.is(BEACON_TRANSPARENT); //Bedrock blocks don't stop beacon beams

				if(!bedrock && blockstate.getLightBlock(world, currPos) >= 15) {
					broke = true;
					break;
				}

				currSegment.increaseHeight();

				if(bedrock)
					continue;
			}

			if(check) {
				boolean added = seenPositions.add(currPos);
				if(!added) {
					broke = true;
					break;
				}

			}
		}

		if(horizontalMoves == 0 || currPos.getY() <= world.getMinBuildHeight())
			broke = true;

		final String tag = "quark:redirected";

		if(!broke) {
			beacon.checkingBeamSections.add(currSegment);
			beacon.lastCheckY = targetHeight + 1;
		} else {
			beacon.getPersistentData().putBoolean(tag, false);

			beacon.checkingBeamSections.clear();
			beacon.lastCheckY = targetHeight;
		}

		if(!beacon.getPersistentData().getBoolean(tag) && didRedirection && !beacon.checkingBeamSections.isEmpty()) {
			beacon.getPersistentData().putBoolean(tag, true);

			int i = beaconPos.getX();
			int j = beaconPos.getY();
			int k = beaconPos.getZ();
            for(ServerPlayer serverplayer : beacon.getLevel().getEntitiesOfClass(ServerPlayer.class, (new AABB((double)i, (double)j, (double)k, (double)i, (double)(j - 4), (double)k)).inflate(10.0D, 5.0D, 10.0D)))
                redirectTrigger.trigger(serverplayer);
		}

		return Integer.MAX_VALUE;
	}

	private static boolean isRedirectingBlock(Block block) {
		return CorundumModule.staticEnabled ? block instanceof CorundumClusterBlock : block == Blocks.AMETHYST_CLUSTER;
	}

	private static float[] getTargetColor(Block block) {
		return block instanceof CorundumClusterBlock cc? cc.base.colorComponents : new float[] { 1F, 1F, 1F };
	}

	public static class ExtendedBeamSegment extends BeaconBeamSection {

		public final Direction dir;
		public final Vec3i offset;
		public final float alpha;

		private boolean isTurn = false;

		public ExtendedBeamSegment(Direction dir, Vec3i offset, float[] colorsIn, float alpha) {
			super(colorsIn);
			this.offset = offset;
			this.dir = dir;
			this.alpha = alpha;
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

		public void setHeight(int target) {
			height = target;
		}

	}

}
