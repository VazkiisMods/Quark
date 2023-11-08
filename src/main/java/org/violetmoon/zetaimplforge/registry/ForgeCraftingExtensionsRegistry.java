package org.violetmoon.zetaimplforge.registry;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import org.violetmoon.zeta.recipe.IZetaCondition;
import org.violetmoon.zeta.recipe.IZetaConditionSerializer;
import org.violetmoon.zeta.recipe.IZetaIngredientSerializer;
import org.violetmoon.zeta.registry.CraftingExtensionsRegistry;

import java.util.HashMap;
import java.util.Map;

public class ForgeCraftingExtensionsRegistry implements CraftingExtensionsRegistry {
	public final Map<IZetaIngredientSerializer<?>, IIngredientSerializer<?>> toForgeIngredientSerializers = new HashMap<>();

	@Override
	public IZetaConditionSerializer<?> registerConditionSerializer(IZetaConditionSerializer<?> serializer) {
		CraftingHelper.register(toForgeConditionSerializer(serializer));
		return serializer;
	}

	@Override
	public <T extends Ingredient> IZetaIngredientSerializer<T> registerIngredientSerializer(ResourceLocation id, IZetaIngredientSerializer<T> serializer) {
		IIngredientSerializer<T> forge = toForgeIngredientSerializer(serializer);

		toForgeIngredientSerializers.put(serializer, forge);
		CraftingHelper.register(id, forge);

		return serializer;
	}

	@Override
	public ResourceLocation getID(IZetaIngredientSerializer<?> serializer) {
		return CraftingHelper.getID(toForgeIngredientSerializers.get(serializer));
	}

	protected ICondition.IContext toForgeContext(IZetaCondition.IContext mine) {
		return mine::getAllTags;
	}

	protected IZetaCondition.IContext fromForgeContext(ICondition.IContext theirs) {
		return theirs::getAllTags;
	}

	//TODO: very sketchy
	@SuppressWarnings("unchecked")
	protected <THEIR_CONDITION extends ICondition, MY_CONDITION extends IZetaCondition> THEIR_CONDITION toForgeCondition(MY_CONDITION mine) {
		return (THEIR_CONDITION) new ICondition() {
			@Override
			public ResourceLocation getID() {
				return mine.getID();
			}

			@Override
			public boolean test(IContext context) {
				return mine.test(fromForgeContext(context));
			}
		};
	}

	@SuppressWarnings("unchecked")
	protected <THEIR_CONDITION extends ICondition, MY_CONDITION extends IZetaCondition> MY_CONDITION fromForgeCondition(THEIR_CONDITION theirs) {
		return (MY_CONDITION) new IZetaCondition() {
			@Override
			public ResourceLocation getID() {
				return theirs.getID();
			}

			@Override
			public boolean test(IContext context) {
				return theirs.test(toForgeContext(context));
			}
		};
	}

	protected <THEIR_CONDITION extends ICondition, MY_CONDITION extends IZetaCondition> IConditionSerializer<THEIR_CONDITION> toForgeConditionSerializer(IZetaConditionSerializer<MY_CONDITION> mine) {
		return new IConditionSerializer<>() {
			@Override
			public void write(JsonObject json, THEIR_CONDITION value) {
				mine.write(json, fromForgeCondition(value));
			}

			@Override
			public THEIR_CONDITION read(JsonObject json) {
				return toForgeCondition(mine.read(json));
			}

			@Override
			public ResourceLocation getID() {
				return mine.getID();
			}
		};
	}

	protected <T extends Ingredient> IIngredientSerializer<T> toForgeIngredientSerializer(IZetaIngredientSerializer<T> mine) {
		return new IIngredientSerializer<>() {
			@Override
			public T parse(FriendlyByteBuf buffer) {
				return mine.parse(buffer);
			}

			@Override
			public T parse(JsonObject json) {
				return mine.parse(json);
			}

			@Override
			public void write(FriendlyByteBuf buffer, T ingredient) {
				mine.write(buffer, ingredient);
			}
		};
	}
}
