/**
 * This class was created by <Darkhax>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 * 
 * File Created @ [06/06/2016, 17:06:43 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class SheepArmor extends Feature {

	public static AttributeModifier sheepArmor = new AttributeModifier(UUID.fromString("6e915cea-3f18-485d-a818-373fe4f75f7f"), "sheep_armor", 1.0d, 0);
	
	@Override
	public void setupConfig() {
		double armorAmount = loadPropDouble("Sheep Armor Amount", "The amount of armor points to give to a sheep when it is not sheared.", 1.0d);
		sheepArmor = new AttributeModifier(UUID.fromString("6e915cea-3f18-485d-a818-373fe4f75f7f"), "sheep_armor", armorAmount, 0);
	}
	
	@SubscribeEvent
	public void onUpdate(LivingUpdateEvent event) {
		if(event.getEntityLiving() instanceof EntitySheep) {
			EntitySheep entity = (EntitySheep) event.getEntityLiving();
			ModifiableAttributeInstance armorAttribute = (ModifiableAttributeInstance) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR);
			boolean hasModifier = armorAttribute.hasModifier(sheepArmor);
			boolean isSheared = entity.getSheared();
			
			if (!isSheared && !hasModifier)
				armorAttribute.applyModifier(sheepArmor);
			
			else if (isSheared && hasModifier)
				armorAttribute.removeModifier(sheepArmor);
		}
	}
	
	@Override
	public boolean hasSubscriptions() {
		return true;
	}
}