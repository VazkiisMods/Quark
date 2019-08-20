package vazkii.quark.vanity.module;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraft.world.storage.loot.TagLootEntry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.api.IRuneColorProvider;
import vazkii.quark.api.QuarkCapabilities;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.module.Config;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.vanity.item.RainbowRuneItem;
import vazkii.quark.vanity.item.RuneItem;

/**
 * @author WireSegal
 * Created at 1:52 PM on 8/17/19.
 */
@LoadModule(category = ModuleCategory.VANITY, hasSubscriptions = true)
public class ColorRunesModule extends Module {

	public static final String TAG_RUNE_ATTACHED = Quark.MOD_ID + ":RuneAttached";
	public static final String TAG_RUNE_COLOR = Quark.MOD_ID + ":RuneColor";

	private static final ThreadLocal<ItemStack> targetStack = new ThreadLocal<>();
	private static Tag<Item> runesTag;

	@Config public static int dungeonWeight = 20;
	@Config public static int netherFortressWeight = 15;
	@Config public static int jungleTempleWeight = 15;
	@Config public static int desertTempleWeight = 15;
	@Config public static int itemQuality = 0;
	@Config public static int applyCost = 15;

	public static void setTargetStack(ItemStack stack) {
		targetStack.set(stack);
	}

	public static int changeColor(int color) {
		ItemStack target = targetStack.get();
		if (target == null)
			return color;

		LazyOptional<IRuneColorProvider> cap = get(target);

		if (cap.isPresent())
			return cap.orElse((s) -> color).getRuneColor(target);

		if (!ItemNBTHelper.getBoolean(target, TAG_RUNE_ATTACHED, false))
			return color;

		ItemStack proxied = ItemStack.read(ItemNBTHelper.getCompound(target, TAG_RUNE_COLOR, false));

		LazyOptional<IRuneColorProvider> proxyCap = get(proxied);

		return proxyCap.orElse((s) -> color).getRuneColor(target);
	}

	@OnlyIn(Dist.CLIENT)
	public static void applyColor() {
		int color = changeColor(0xFF8040CC);
		if (color != 0xFF8040CC) {
			int a = (color >> 24) & 0xFF;
			int r = (color >> 16) & 0xFF;
			int g = (color >> 8) & 0xFF;
			int b = color & 0xFF;

			GlStateManager.color4f(r / 255f, g / 255f, b / 255f, a / 255f);
		}
	}

	@Override
	public void start() {
		for(DyeColor color : DyeColor.values()) {
			float[] components = color.getColorComponentValues();
			int rgb = 0xFF000000 |
					((int) (255 * components[0]) << 16) |
					((int) (255 * components[1]) << 8) |
					(int) (255 * components[2]);

			new RuneItem(color.getName() + "_rune", this, rgb);
		}
		
		new RainbowRuneItem(this);
	}

	@Override
	public void setup() {
		runesTag = new ItemTags.Wrapper(new ResourceLocation(Quark.MOD_ID, "runes"));
	}

	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event) {
		int weight = 0;

		if(event.getName().equals(LootTables.CHESTS_SIMPLE_DUNGEON))
			weight = dungeonWeight;
		else if(event.getName().equals(LootTables.CHESTS_NETHER_BRIDGE))
			weight = netherFortressWeight;
		else if(event.getName().equals(LootTables.CHESTS_JUNGLE_TEMPLE))
			weight = jungleTempleWeight;
		else if(event.getName().equals(LootTables.CHESTS_DESERT_PYRAMID))
			weight = desertTempleWeight;
		
		if(weight > 0) {
			LootEntry entry = TagLootEntry.func_216176_b(runesTag).weight(weight).quality(itemQuality).func_216081_b();
			MiscUtil.addToLootTable(event.getTable(), entry);
		}
	}

	@SubscribeEvent
	public void onAnvilUpdate(AnvilUpdateEvent event) {
		ItemStack left = event.getLeft();
		ItemStack right = event.getRight();

		if(!left.isEmpty() && !right.isEmpty() && left.isEnchanted() && get(right).isPresent()) {
			ItemStack out = left.copy();
			ItemNBTHelper.setBoolean(out, TAG_RUNE_ATTACHED, true);
			ItemNBTHelper.setCompound(out, TAG_RUNE_COLOR, right.serializeNBT());
			event.setOutput(out);
			event.setCost(applyCost);
			event.setMaterialCost(1);
		}
	}

	private static LazyOptional<IRuneColorProvider> get(ICapabilityProvider provider) {
		return provider.getCapability(QuarkCapabilities.RUNE_COLOR);
	}

}
