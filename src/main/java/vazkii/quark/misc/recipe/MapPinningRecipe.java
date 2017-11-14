package vazkii.quark.misc.recipe;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.arl.recipe.ModRecipe;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.misc.feature.MapMarkers;

public class MapPinningRecipe extends ModRecipe {

	public MapPinningRecipe() {
		super(new ResourceLocation("quark", "map_pinning"));
	}

	@Override
	public boolean matches(InventoryCrafting var1, World var2) {
		boolean foundDye = false;
		boolean foundTarget = false;
		int blackDye = OreDictionary.getOreID("dyeBlack");
		
		for(int i = 0; i < var1.getSizeInventory(); i++) {
			ItemStack stack = var1.getStackInSlot(i);
			if(!stack.isEmpty()) {
				boolean isBlackDye = false;
				int[] ores = OreDictionary.getOreIDs(stack);
				for(int o : ores)
					if(o == blackDye) {
						isBlackDye = true;
						break;
					}
				
				if(stack.getItem() == Items.FILLED_MAP) {
					if(foundTarget)
						return false;
					foundTarget = true;
				} else if(isBlackDye) {
					if(!foundDye)
						foundDye = true;
					else return false;
				}
			}
		}

		return foundDye && foundTarget;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if(stack.getItem() == Items.FILLED_MAP) {
				ItemStack retStack = stack.copy();
				ItemNBTHelper.setBoolean(retStack, MapMarkers.TAG_ADD_PIN, true);
				return retStack;
			}
		}

		return getRecipeOutput();
	}

	@Override
	public boolean canFit(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}


}
