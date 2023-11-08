package org.violetmoon.quark.base.handler;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZEntityAttributeCreation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class EntityAttributeHandler {
	
	private static Map<EntityType<? extends LivingEntity>, Supplier<Builder>> attributeSuppliers = new HashMap<>();
	
	public static void put(EntityType<? extends LivingEntity> type, Supplier<Builder> attrSupplier) {
		attributeSuppliers.put(type, attrSupplier);
	}
	
	@LoadEvent
	public static void onAttributeCreation(ZEntityAttributeCreation event) {
		for(EntityType<? extends LivingEntity> type : attributeSuppliers.keySet()) {
			Supplier<Builder> supplier = attributeSuppliers.get(type);
			event.put(type, supplier.get().build());
		}
	}

}
