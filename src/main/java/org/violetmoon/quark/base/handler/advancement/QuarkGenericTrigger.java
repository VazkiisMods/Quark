package org.violetmoon.quark.base.handler.advancement;

import com.google.common.base.Predicates;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate.Composite;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class QuarkGenericTrigger extends SimpleCriterionTrigger<QuarkGenericTrigger.Instance> {

	final ResourceLocation id;

	public QuarkGenericTrigger(ResourceLocation p_222616_) {
		this.id = p_222616_;
	}

	public void trigger(ServerPlayer p_148030_) {
		trigger(p_148030_, Predicates.alwaysTrue());
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	protected Instance createInstance(JsonObject p_66248_, Composite p_66249_, DeserializationContext p_66250_) {
		return new Instance(id, p_66249_);
	}

	static class Instance extends AbstractCriterionTriggerInstance {

		public Instance(ResourceLocation p_16975_, Composite p_16976_) {
			super(p_16975_, p_16976_);
		}

	}

}
