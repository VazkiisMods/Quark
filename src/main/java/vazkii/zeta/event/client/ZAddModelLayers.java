package vazkii.zeta.event.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import vazkii.zeta.event.bus.IZetaLoadEvent;

public interface ZAddModelLayers extends IZetaLoadEvent {
	<T extends LivingEntity, R extends LivingEntityRenderer<T, ? extends EntityModel<T>>> R getRenderer(EntityType<? extends T> entityType);
	EntityModelSet getEntityModels();
}
