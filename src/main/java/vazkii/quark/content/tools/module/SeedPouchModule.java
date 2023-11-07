package vazkii.quark.content.tools.module;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.tools.client.tooltip.SeedPouchClientTooltipComponent;
import vazkii.quark.content.tools.item.SeedPouchItem;
import vazkii.zeta.client.event.ZClientSetup;
import vazkii.zeta.client.event.ZTooltipComponents;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.ZEntityItemPickup;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;
import vazkii.zeta.util.Hint;

@ZetaLoadModule(category = "tools")
public class SeedPouchModule extends ZetaModule {

	@Hint public static Item seed_pouch;

	public static TagKey<Item> seedPouchHoldableTag;

	@Config public static int maxItems = 640;
	@Config public static boolean showAllVariantsInCreative = true;
	@Config public static int shiftRange = 3;

	@LoadEvent
	public final void register(ZRegister event) {
		seed_pouch = new SeedPouchItem(this);
	}

	@LoadEvent
	public final void setup(ZCommonSetup event) {
		seedPouchHoldableTag = ItemTags.create(new ResourceLocation(Quark.MOD_ID, "seed_pouch_holdable"));
	}

	@PlayEvent
	public void onItemPickup(ZEntityItemPickup event) {
		Player player = event.getEntity();
		ItemStack stack = event.getItem().getItem();

		ItemStack main = player.getMainHandItem();
		ItemStack off = player.getOffhandItem();

		ImmutableSet<ItemStack> stacks = ImmutableSet.of(main, off);
		for(ItemStack heldStack : stacks)
			if(heldStack.getItem() == seed_pouch && heldStack.getCount() == 1) {
				Pair<ItemStack, Integer> contents = SeedPouchItem.getContents(heldStack);
				if(contents != null) {
					ItemStack pouchStack = contents.getLeft();
					if(ItemStack.isSame(pouchStack, stack)) {
						int curr = contents.getRight();
						int missing = maxItems - curr;

						int count = stack.getCount();
						int toAdd = Math.min(missing, count);

						stack.setCount(count - toAdd);
						SeedPouchItem.setCount(heldStack, curr + toAdd);

						if(player.level instanceof ServerLevel)
							player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BUNDLE_INSERT, SoundSource.PLAYERS, 0.2F, (player.level.random.nextFloat() - player.level.random.nextFloat()) * 1.4F + 2.0F);

						if(stack.getCount() == 0)
							break;
					}
				}
			}
	}

	@ZetaLoadModule(clientReplacement = true)
	public static class Client extends SeedPouchModule {
		@LoadEvent
		public void clientSetup(ZClientSetup e) {
			e.enqueueWork(() -> ItemProperties.register(seed_pouch, new ResourceLocation("pouch_items"), SeedPouchItem::itemFraction));
		}

		@LoadEvent
		public void registerClientTooltipComponentFactories(ZTooltipComponents event) {
			event.register(SeedPouchItem.Tooltip.class, t -> new SeedPouchClientTooltipComponent(t.stack()));
		}
	}
}
