package vazkii.quark.content.tweaks.module;

import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.hint.Hint;

/**
 * @author WireSegal
 * Created at 11:25 AM on 9/2/19.
 */
@LoadModule(category = "tweaks", hasSubscriptions = true)
public class VillagersFollowEmeraldsModule extends QuarkModule {

	@Hint Item emerald_block = Items.EMERALD_BLOCK;

	@SubscribeEvent
	public void onVillagerAppear(EntityJoinLevelEvent event) {
		if(event.getEntity() instanceof Villager villager) {
			boolean alreadySetUp = villager.goalSelector.getAvailableGoals().stream().anyMatch((goal) -> goal.getGoal() instanceof TemptGoal);

			if (!alreadySetUp)
				try {
					MiscUtil.addGoalJustAfterLatestWithPriority(villager.goalSelector, 2, new TemptGoal(villager, 0.6, Ingredient.of(Items.EMERALD_BLOCK), false));
				} catch(IllegalArgumentException e) {
					// This appears to be a weird bug that happens when a villager is riding something and its chunk unloads
				}

		}
	}
}
