package vazkii.quark.content.tweaks.module;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = "tweaks", hasSubscriptions = true)
public class SaferCreaturesModule extends QuarkModule {

	@Config(description = "How many blocks should be subtracted from the rabbit fall height when calculating fall damage. 5 is the same value as vanilla frogs") 
	public double heightReduction = 5.0;
	
	@Config
	public boolean enableSlimeFallDamageRemoval = true;
	
	@SubscribeEvent
	public void onFall(LivingFallEvent event) {
		Entity e = event.getEntity();
		EntityType<?> type = e.getType();
		float dist = event.getDistance();
		
		if(type == EntityType.RABBIT)
			event.setDistance(Math.max(0, dist - (float) heightReduction));
		
		else if(type == EntityType.SLIME && enableSlimeFallDamageRemoval) {
			if(dist > 2) {
				Vec3 movement = e.getDeltaMovement();
				e.setDeltaMovement(movement.x, -2, movement.z);
			}
			
			event.setDistance(0);
		}
	}
	
}
