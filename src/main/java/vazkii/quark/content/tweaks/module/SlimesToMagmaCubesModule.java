package vazkii.quark.content.tweaks.module;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.hint.Hint;

@LoadModule(category = "tweaks", hasSubscriptions = true)
public class SlimesToMagmaCubesModule extends QuarkModule {

	private static final String TAG_MAGMAED = "quark:damaged_by_magma";

	@Hint Item magma_cream = Items.MAGMA_CREAM;

	public static boolean staticEnabled;

	@Override
	public void configChanged() {
		staticEnabled = enabled;
	}

	@SubscribeEvent
	public void onDeath(LivingDeathEvent event) {
		if(event.getEntity().getType() == EntityType.SLIME && event.getSource() == DamageSource.HOT_FLOOR)
			event.getEntity().getPersistentData().putBoolean(TAG_MAGMAED, true);
	}

	public static EntityType<? extends Slime> getSlimeType(EntityType<? extends Slime> prev, Slime slime) {
		if(!staticEnabled)
			return prev;

		if(slime.getPersistentData().getBoolean(TAG_MAGMAED))
			return EntityType.MAGMA_CUBE;

		return prev;
	}

}
