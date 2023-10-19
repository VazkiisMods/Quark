package vazkii.quark.content.tweaks.module;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.api.event.RecipeCrawlEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.tweaks.recipe.SlabToBlockRecipe;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;

import java.util.HashMap;
import java.util.Map;

@LoadModule(category = "tweaks", hasSubscriptions = true)
public class SlabsToBlocksModule extends QuarkModule {

	public static Map<Item, Item> recipes = new HashMap<>();

	@LoadEvent
	public final void register(ZRegister event) {
		ForgeRegistries.RECIPE_SERIALIZERS.register(Quark.MOD_ID + ":slab_to_block", SlabToBlockRecipe.SERIALIZER);
	}

	@SubscribeEvent
	public void onReset(RecipeCrawlEvent.Reset event) {
		recipes.clear();
	}

	private ItemStack extract(ItemStack[] array) {
		if (array.length == 0)
			return ItemStack.EMPTY;
		return array[0];
	}

	@SubscribeEvent
	public void onVisitShaped(RecipeCrawlEvent.Visit.Shaped visit) {
		if(visit.ingredients.size() == 3
				&& visit.recipe.getHeight() == 1
				&& visit.recipe.getWidth() == 3
				&& visit.output.getItem() instanceof BlockItem bi
				&& bi.getBlock() instanceof SlabBlock) {

			Item a = extract(visit.ingredients.get(0).getItems()).getItem();
			Item b = extract(visit.ingredients.get(1).getItems()).getItem();
			Item c = extract(visit.ingredients.get(2).getItems()).getItem();

			if(a == b && b == c && a != Items.AIR)
				recipes.put(bi, a);
		}
	}

}
