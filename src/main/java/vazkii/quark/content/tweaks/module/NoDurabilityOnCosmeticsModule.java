package vazkii.quark.content.tweaks.module;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.config.Config;
import vazkii.zeta.event.ZAnvilRepair;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;

@ZetaLoadModule(category = "tweaks")
public class NoDurabilityOnCosmeticsModule extends ZetaModule {

	@Config(description = "Allow applying cosmetic items such as color runes with no anvil durability usage? Cosmetic items are defined in the quark:cosmetic_anvil_items tag") 
	private boolean allowCosmeticItems = true;
	
	public static TagKey<Item> cosmeticTag;

	@LoadEvent
	public final void setup(ZCommonSetup event) {
		cosmeticTag = ItemTags.create(new ResourceLocation(Quark.MOD_ID, "cosmetic_anvil_items"));
	}
	
	@PlayEvent
	public void onAnvilUse(ZAnvilRepair event) {
		ItemStack right = event.getRight();
		
		if(right.isEmpty() || (allowCosmeticItems && right.is(cosmeticTag)))
			event.setBreakChance(0F);
	}
	
}
