package org.violetmoon.quark.base.config;

import java.util.function.BooleanSupplier;

import org.violetmoon.zeta.recipe.IZetaCondition;
import org.violetmoon.zeta.recipe.IZetaConditionSerializer;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

/**
 * @author WireSegal
 * Created at 1:23 PM on 8/24/19.
 */
public record FlagCondition(ConfigFlagManager cfm, String flag, ResourceLocation loc, BooleanSupplier extraCondition) implements IZetaCondition {

	@Override
	public ResourceLocation getID() {
		return loc;
	}

	@Override
	public boolean test(IContext context) {
		if (flag.contains("%"))
			throw new RuntimeException("Illegal flag: " + flag);

		if (!cfm.isValidFlag(flag))
			cfm.zeta.log.warn("Non-existent flag " + flag + " being used");

		return extraCondition.getAsBoolean() && cfm.getFlag(flag);
	}

	public static class Serializer implements IZetaConditionSerializer<FlagCondition> {
		private final ConfigFlagManager cfm;
		private final ResourceLocation location;
		private final BooleanSupplier extraCondition;

		public Serializer(ConfigFlagManager cfm, ResourceLocation location, BooleanSupplier extraCondition) {
			this.cfm = cfm;
			this.location = location;
			this.extraCondition = extraCondition;
		}

		public Serializer(ConfigFlagManager cfm, ResourceLocation location) {
			this(cfm, location, () -> true);
		}

		@Override
		public void write(JsonObject json, FlagCondition value) {
			json.addProperty("flag", value.flag);
		}

		@Override
		public FlagCondition read(JsonObject json) {
			return new FlagCondition(cfm, json.getAsJsonPrimitive("flag").getAsString(), location, extraCondition);
		}

		@Override
		public ResourceLocation getID() {
			return location;
		}
	}
}
