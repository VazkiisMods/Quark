package vazkii.quark.addons.oddities.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Registry;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.extensions.IForgeMenuType;
import vazkii.quark.addons.oddities.client.screen.BackpackInventoryScreen;
import vazkii.quark.addons.oddities.inventory.BackpackMenu;
import vazkii.quark.addons.oddities.item.BackpackItem;
import vazkii.quark.base.Quark;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.oddities.HandleBackpackMessage;
import vazkii.zeta.client.event.ZClientSetup;
import vazkii.zeta.client.event.ZScreen;
import vazkii.zeta.client.event.ZStartClientTick;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.ZLivingDrops;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;
import vazkii.zeta.util.Hint;

@ZetaLoadModule(category = "oddities")
public class BackpackModule extends ZetaModule {

	@Config(description = "Set this to true to allow the backpacks to be unequipped even with items in them")
	public static boolean superOpMode = false;

	@Config(flag = "ravager_hide")
	public static boolean enableRavagerHide = true;

	@Config
	public static boolean itemsInBackpackTick = true;

	@Config public static int baseRavagerHideDrop = 1;
	@Config public static double extraChancePerLooting = 0.5;

	@Hint public static Item backpack;
	@Hint("ravager_hide") public static Item ravager_hide;

	public static Block bonded_ravager_hide;

	public static TagKey<Item> backpackBlockedTag;
	
	public static MenuType<BackpackMenu> menyType;
	private static ItemStack heldStack = null;

	@LoadEvent
	public final void register(ZRegister event) {
		backpack = new BackpackItem(this);
		ravager_hide = new QuarkItem("ravager_hide", this, new Item.Properties().rarity(Rarity.RARE).tab(CreativeModeTab.TAB_MATERIALS)).setCondition(() -> enableRavagerHide);

		menyType = IForgeMenuType.create(BackpackMenu::fromNetwork);
		Quark.ZETA.registry.register(menyType, "backpack", Registry.MENU_REGISTRY);

		bonded_ravager_hide = new QuarkBlock("bonded_ravager_hide", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Block.Properties.of(Material.WOOL, DyeColor.BLACK)
				.strength(1F)
				.sound(SoundType.WOOL))
		.setCondition(() -> enableRavagerHide);
		
		CauldronInteraction.WATER.put(backpack, CauldronInteraction.DYED_ITEM);
	}
	
	@LoadEvent
	public final void setup(ZCommonSetup event) {
		backpackBlockedTag = ItemTags.create(new ResourceLocation(Quark.MOD_ID, "backpack_blocked"));
	}

	@PlayEvent
	public void onDrops(ZLivingDrops event) {
		LivingEntity entity = event.getEntity();
		if(enableRavagerHide && entity.getType() == EntityType.RAVAGER) {
			int amount = baseRavagerHideDrop;
			double chance = (double) event.getLootingLevel() * extraChancePerLooting;
			while(chance > baseRavagerHideDrop) {
				chance--;
				amount++;
			}
			if(chance > 0 && entity.level.random.nextDouble() < chance)
				amount++;

			event.getDrops().add(new ItemEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(ravager_hide, amount)));
		}
	}

	public static void requestBackpack() {
		heldStack = Minecraft.getInstance().player.inventoryMenu.getCarried();
		QuarkNetwork.sendToServer(new HandleBackpackMessage(true));
	}

	public static boolean isEntityWearingBackpack(Entity e) {
		if(e instanceof LivingEntity living) {
			ItemStack chestArmor = living.getItemBySlot(EquipmentSlot.CHEST);
			return chestArmor.getItem() instanceof BackpackItem;
		}

		return false;
	}

	public static boolean isEntityWearingBackpack(Entity e, ItemStack stack) {
		if(e instanceof LivingEntity living) {
			ItemStack chestArmor = living.getItemBySlot(EquipmentSlot.CHEST);
			return chestArmor == stack;
		}

		return false;
	}

	@ZetaLoadModule(clientReplacement = true)
	public static class Client extends BackpackModule {
		private static boolean backpackRequested;

		@LoadEvent
		public void clientSetup(ZClientSetup e) {
			MenuScreens.register(menyType, BackpackInventoryScreen::new);

			e.enqueueWork(() -> ItemProperties.register(backpack, new ResourceLocation("has_items"),
					(stack, world, entity, i) -> (!BackpackModule.superOpMode && BackpackItem.doesBackpackHaveItems(stack)) ? 1 : 0));
		}

		@PlayEvent
		public void onOpenGUI(ZScreen.Opening event) {
			Player player = Minecraft.getInstance().player;
			if(player != null && isInventoryGUI(event.getScreen()) && !player.getAbilities().instabuild && isEntityWearingBackpack(player) && !player.isInsidePortal) {
				requestBackpack();
				event.setCanceled(true);
			}
		}

		@PlayEvent
		//fixme this was ClientTickEvent before so not sure if this is correct
		public void clientTick(ZStartClientTick event) {
			Minecraft mc = Minecraft.getInstance();
			if(isInventoryGUI(mc.screen) && !backpackRequested && isEntityWearingBackpack(mc.player) && !mc.player.isInsidePortal) {
				requestBackpack();
				mc.player.inventoryMenu.setCarried(mc.player.getItemBySlot(EquipmentSlot.CHEST));
				backpackRequested = true;
			} else if(mc.screen instanceof BackpackInventoryScreen) {
				if(heldStack != null) {
					mc.player.inventoryMenu.setCarried(heldStack);
					heldStack = null;
				}

				backpackRequested = false;
			}
		}

		private static boolean isInventoryGUI(Screen gui) {
			return gui != null && gui.getClass() == InventoryScreen.class;
		}
	}
}
