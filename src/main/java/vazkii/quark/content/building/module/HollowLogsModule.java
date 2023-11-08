package vazkii.quark.content.building.module;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.api.ICrawlSpaceBlock;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.advancement.QuarkAdvancementHandler;
import vazkii.quark.base.handler.advancement.QuarkGenericTrigger;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.util.VanillaWoods;
import vazkii.quark.base.util.VanillaWoods.Wood;
import vazkii.quark.content.building.block.HollowLogBlock;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.ZPlayerTick;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;
import vazkii.zeta.util.Hint;

@ZetaLoadModule(category = "building")
public class HollowLogsModule extends ZetaModule {

	private static final String TAG_TRYING_TO_CRAWL = "quark:trying_crawl";

	public static QuarkGenericTrigger crawlTrigger;

	@Config(flag = "hollow_log_auto_crawl")
	public static boolean enableAutoCrawl = true;

	@Hint(key = "hollow_logs", value = "hollow_log_auto_crawl")
	TagKey<Block> hollowLogsTag;

	@LoadEvent
	public final void register(ZRegister event) {
		for(Wood wood : VanillaWoods.ALL) {
			new HollowLogBlock(wood.log(), this, !wood.nether());
//			new HollowWoodBlock(wood.wood(), this, !wood.nether());
		}

		crawlTrigger = QuarkAdvancementHandler.registerGenericTrigger("hollow_log_crawl");
	}

	@LoadEvent
	public final void setup(ZCommonSetup event) {
		hollowLogsTag = BlockTags.create(new ResourceLocation(Quark.MOD_ID, "hollow_logs"));
	}

	@PlayEvent
	public void playerTick(ZPlayerTick.Start event) {
		if(enableAutoCrawl) {
			Player player = event.getPlayer();
			BlockPos playerPos = player.blockPosition();
			boolean isTrying = player.isVisuallyCrawling() ||
				(player.isCrouching() && !player.isColliding(playerPos, player.level.getBlockState(playerPos)));
			boolean wasTrying = player.getPersistentData().getBoolean(TAG_TRYING_TO_CRAWL);

			if (!player.isVisuallyCrawling()) {
				if (isTrying && !wasTrying) {
					Direction dir = player.getDirection();
					Direction opp = dir.getOpposite();
					if (dir.getAxis() != Axis.Y) {
						BlockPos pos = playerPos.relative(dir);

						if (!tryClimb(player, opp, playerPos)) // Crawl out
							if (!tryClimb(player, opp, playerPos.above())) // Crawl out
								if (!tryClimb(player, dir, pos)) // Crawl into
									tryClimb(player, dir, pos.above()); // Crawl into
					}
				}
			}

			if(isTrying != wasTrying)
				player.getPersistentData().putBoolean(TAG_TRYING_TO_CRAWL, isTrying);
		}
	}

	private boolean tryClimb(Player player, Direction dir, BlockPos pos) {
		BlockState state = player.level.getBlockState(pos);
		Block block = state.getBlock();

		if(block instanceof ICrawlSpaceBlock crawlSpace) {
			if(crawlSpace.canCrawl(player.level, state, pos, dir)) {
				player.setPose(Pose.SWIMMING);
				player.setSwimming(true);

				double x = pos.getX() + 0.5 - (dir.getStepX() * 0.3);
				double y = pos.getY() + crawlSpace.crawlHeight(player.level, state, pos, dir);
				double z = pos.getZ() + 0.5 - (dir.getStepZ() * 0.3);

				player.setPos(x, y, z);

				if(player instanceof ServerPlayer sp && crawlSpace.isLog(sp, state, pos, dir))
					crawlTrigger.trigger(sp);

				return true;
			}
		}

		return false;
	}
}
