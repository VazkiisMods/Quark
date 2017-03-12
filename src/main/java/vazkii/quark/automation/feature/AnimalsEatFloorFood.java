package vazkii.quark.automation.feature;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class AnimalsEatFloorFood extends Feature {

	@SubscribeEvent
	public void onEntityTick(LivingUpdateEvent event) {
		if(event.getEntityLiving() instanceof EntityAnimal) {
			EntityAnimal animal = (EntityAnimal) event.getEntityLiving();
			if(animal.getGrowingAge() == 0 && !animal.isInLove() && !animal.isDead) {
				double range = 2;
				List<EntityItem> nearbyFood = animal.getEntityWorld().getEntitiesWithinAABB(EntityItem.class, animal.getEntityBoundingBox().expand(range, 0, range),
						(EntityItem i) -> !i.getEntityItem().isEmpty() && !i.isDead && animal.isBreedingItem(i.getEntityItem()));
				
				if(!nearbyFood.isEmpty()) {
					EntityItem e = nearbyFood.get(0);
					
					ItemStack stack = e.getEntityItem();
					stack.shrink(1);;
					e.setEntityItemStack(stack);
					if(stack.isEmpty())
						e.setDead();
					
					animal.setInLove(null);
				}
			}
		}
	}
	
	@Override
	public String[] getIncompatibleMods() {
		return new String[] { "betterwithmods", "easyBreeding" };
	}
	
	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
}
