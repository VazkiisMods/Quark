package vazkii.quark.content.tweaks.module;

import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.zeta.event.ZEntityJoinLevel;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;
import vazkii.zeta.util.Hint;

/**
 * @author WireSegal
 * Created at 11:25 AM on 9/2/19.
 */
@ZetaLoadModule(category = "tweaks")
public class VillagersFollowEmeraldsModule extends ZetaModule {

	@Hint Item emerald_block = Items.EMERALD_BLOCK;

	@PlayEvent
	public void onVillagerAppear(ZEntityJoinLevel event) {
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
