package org.violetmoon.zetaimplforge.event.load;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import org.violetmoon.zeta.event.load.ZEntityAttributeCreation;

public class ForgeZEntityAttributeCreation implements ZEntityAttributeCreation {
	private final EntityAttributeCreationEvent e;

	public ForgeZEntityAttributeCreation(EntityAttributeCreationEvent e) {
		this.e = e;
	}

	@Override
	public void put(EntityType<? extends LivingEntity> entity, AttributeSupplier map) {
		e.put(entity, map);
	}
}
