package org.violetmoon.quark.content.tweaks.module;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.violetmoon.quark.base.item.QuarkItem;
import org.violetmoon.quark.content.tweaks.recipe.ElytraDuplicationRecipe;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.event.play.entity.living.ZLivingTick;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.Hint;

@ZetaLoadModule(category = "tweaks")
public class DragonScalesModule extends ZetaModule {

	@Hint public static Item dragon_scale;

	@LoadEvent
	public final void register(ZRegister event) {
		event.getRegistry().register(ElytraDuplicationRecipe.SERIALIZER, "elytra_duplication", Registry.RECIPE_SERIALIZER_REGISTRY);

		dragon_scale = new QuarkItem("dragon_scale", this, new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS));
	}

	@PlayEvent
	public void onEntityTick(ZLivingTick event) {
		if(event.getEntity() instanceof EnderDragon dragon && !event.getEntity().getCommandSenderWorld().isClientSide) {
			if(dragon.getDragonFight() != null && dragon.getDragonFight().hasPreviouslyKilledDragon() && dragon.dragonDeathTime == 100) {
				Vec3 pos = dragon.position();
				ItemEntity item = new ItemEntity(dragon.level, pos.x, pos.y, pos.z, new ItemStack(dragon_scale, 1));
				dragon.level.addFreshEntity(item);
			}
		}
	}

}
