package org.violetmoon.quark.content.tweaks.module;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.config.Config;
import org.violetmoon.quark.base.handler.MiscUtil;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.event.play.ZBlock;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.Hint;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolActions;

@ZetaLoadModule(category = "tweaks")
public class HoeHarvestingModule extends ZetaModule {

	@Config
	@Config.Min(1)
	@Config.Max(5)
	public static int regularHoeRadius = 2;

	@Config
	@Config.Min(1)
	@Config.Max(5)
	public static int highTierHoeRadius = 3;


	@Hint(key = "hoe_harvesting")
	TagKey<Item> hoes = Tags.Items.TOOLS_HOES;

	public static TagKey<Item> bigHarvestingHoesTag;

	public static int getRange(ItemStack hoe) {
		if(!Quark.ZETA.modules.isEnabled(HoeHarvestingModule.class))
			return 1;

		if(!isHoe(hoe))
			return 1;
		else if (hoe.is(bigHarvestingHoesTag))
			return highTierHoeRadius;
		else
			return regularHoeRadius;
	}

	public static boolean isHoe(ItemStack itemStack) {
		return !itemStack.isEmpty() &&
				(itemStack.getItem() instanceof HoeItem
						|| itemStack.is(Tags.Items.TOOLS_HOES)
						|| itemStack.getItem().canPerformAction(itemStack, ToolActions.HOE_DIG));
	}

	@LoadEvent
	public final void setup(ZCommonSetup event) {
		bigHarvestingHoesTag = ItemTags.create(new ResourceLocation(Quark.MOD_ID, "big_harvesting_hoes"));
	}

	@PlayEvent
	public void onBlockBroken(ZBlock.Break event) {
		LevelAccessor world = event.getLevel();
		if(!(world instanceof Level level))
			return;

		Player player = event.getPlayer();
		BlockPos basePos = event.getPos();
		ItemStack stack = player.getMainHandItem();
		if (isHoe(stack) && canHarvest(player, world, basePos, event.getState())) {
			boolean brokeNonInstant = false;
			int range = getRange(stack);

			for (int i = 1 - range; i < range; i++)
				for (int k = 1 - range; k < range; k++) {
					if (i == 0 && k == 0)
						continue;

					BlockPos pos = basePos.offset(i, 0, k);
					BlockState state = world.getBlockState(pos);
					if (canHarvest(player, world, pos, state)) {
						Block block = state.getBlock();

						if (state.getDestroySpeed(world, pos) != 0.0F)
							brokeNonInstant = true;
						if (block.canHarvestBlock(state, world, pos, player))
							block.playerDestroy(level, player, pos, state, world.getBlockEntity(pos), stack);
						world.destroyBlock(pos, false);
						world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
					}
				}

			if (brokeNonInstant)
				MiscUtil.damageStack(player, InteractionHand.MAIN_HAND, stack, 1);
		}
	}

	private boolean canHarvest(Player player, LevelAccessor world, BlockPos pos, BlockState state) {
		Block block = state.getBlock();
		if(block instanceof IPlantable plant) {
			PlantType type = plant.getPlantType(world, pos);
			return type != PlantType.WATER && type != PlantType.DESERT;
		}

		Material mat = state.getMaterial();
		boolean isHarvestableMaterial =
					mat == Material.PLANT ||
					mat == Material.REPLACEABLE_FIREPROOF_PLANT ||
					mat == Material.REPLACEABLE_PLANT ||
					mat == Material.WATER_PLANT;
		return isHarvestableMaterial &&
				state.canBeReplaced(new BlockPlaceContext(new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(new Vec3(0.5, 0.5, 0.5), Direction.DOWN, pos, false))));
	}

}
