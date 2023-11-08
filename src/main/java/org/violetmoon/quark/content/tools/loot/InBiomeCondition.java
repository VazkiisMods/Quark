package org.violetmoon.quark.content.tools.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.phys.Vec3;
import org.violetmoon.quark.content.tools.module.PathfinderMapsModule;

import javax.annotation.Nonnull;

public record InBiomeCondition(ResourceLocation target) implements LootItemCondition {

	@Override
	public boolean test(LootContext lootContext) {
		Vec3 pos = lootContext.getParam(LootContextParams.ORIGIN);
		return lootContext.getLevel().getBiome(new BlockPos(pos)).is(target);
	}

	@Override
	@Nonnull
	public LootItemConditionType getType() {
		return PathfinderMapsModule.inBiomeConditionType;
	}

	public static class InBiomeSerializer implements Serializer<InBiomeCondition> {
		@Override
		public void serialize(@Nonnull JsonObject object, @Nonnull InBiomeCondition condition, @Nonnull JsonSerializationContext serializationContext) {
			object.addProperty("target", condition.target.toString());
		}

		@Override
		@Nonnull
		public InBiomeCondition deserialize(@Nonnull JsonObject object, @Nonnull JsonDeserializationContext deserializationContext) {
			String key = GsonHelper.getAsString(object, "target");
			ResourceLocation target = new ResourceLocation(key);

			return new InBiomeCondition(target);
		}
	}
}
