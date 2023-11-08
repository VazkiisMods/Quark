package org.violetmoon.quark.content.tweaks.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.violetmoon.quark.content.tweaks.module.SlabsToBlocksModule;

import java.util.Optional;

public class SlabToBlockRecipe extends CustomRecipe {
	
	public static final SimpleRecipeSerializer<?> SERIALIZER = new SimpleRecipeSerializer<>(SlabToBlockRecipe::new);
	private boolean locked = false;
	
	public SlabToBlockRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean matches(CraftingContainer container, Level level) {
		if(locked)
			return false;
		
		Item target = null;
		
		boolean checked = false;
		boolean result = false;
		
		for(int i = 0; i < container.getContainerSize(); i++) {
			ItemStack stack = container.getItem(i);
			if(!stack.isEmpty()) {
				Item item = stack.getItem();
				
				if(target != null) {
					if(checked)
						return false;
					
					result = item == target && checkForOtherRecipes(container, level);
					checked = true;
				} else {
					if(SlabsToBlocksModule.recipes.containsKey(item)) {
						target = item;
					} else return false;
				}
			}
		}
		
		return result;
	}
	
	// very much doubt multiple threads would ever touch this but JUST IN CASE
	private synchronized boolean checkForOtherRecipes(CraftingContainer container, Level level) {
		locked = true;
		boolean ret = false;
		MinecraftServer server = level.getServer();
		if(server != null) {
			Optional<CraftingRecipe> optional = server.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, container, level);
			ret = !optional.isPresent();
		}
		
		locked = false;
		return ret;
	}

	@Override
	public ItemStack assemble(CraftingContainer container) {
		for(int i = 0; i < container.getContainerSize(); i++) {
			ItemStack stack = container.getItem(i);
			if(!stack.isEmpty()) {
				Item item = stack.getItem();
				
				if(SlabsToBlocksModule.recipes.containsKey(item))
					return new ItemStack(SlabsToBlocksModule.recipes.get(item));
			}
		}
		
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isSpecial() {
		return true;
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return (width * height) >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}



}
