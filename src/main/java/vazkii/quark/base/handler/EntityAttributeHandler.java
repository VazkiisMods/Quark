package vazkii.quark.base.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class EntityAttributeHandler {
	
	private static Map<EntityType<? extends LivingEntity>, Supplier<Builder>> attributeSuppliers = new HashMap<>();
	
	public static void put(EntityType<? extends LivingEntity> type, Supplier<Builder> attrSupplier) {
		attributeSuppliers.put(type, attrSupplier);
	}
	
	@SubscribeEvent
	public static void onAttributeCreation(EntityAttributeCreationEvent event) {
		for(EntityType<? extends LivingEntity> type : attributeSuppliers.keySet()) {
			Supplier<Builder> supplier = attributeSuppliers.get(type);
			event.put(type, supplier.get().build());
		}
	}

}
