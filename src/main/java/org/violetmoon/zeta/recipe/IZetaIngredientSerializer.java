package org.violetmoon.zeta.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.violetmoon.zeta.Zeta;

// Copy of Forge IIngredientSerializer
public interface IZetaIngredientSerializer<T extends Ingredient> {
	T parse(FriendlyByteBuf buffer);

	T parse(JsonObject json);

	void write(FriendlyByteBuf buffer, T ingredient);

	//Aaaaaaaa
	Zeta getZeta();

	default ResourceLocation getID() {
		return getZeta().craftingExtensions.getID(this);
	}
}
