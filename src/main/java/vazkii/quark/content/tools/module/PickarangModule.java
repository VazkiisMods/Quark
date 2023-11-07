package vazkii.quark.content.tools.module;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.advancement.QuarkAdvancementHandler;
import vazkii.quark.base.handler.advancement.QuarkGenericTrigger;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.tools.client.render.entity.PickarangRenderer;
import vazkii.quark.content.tools.config.PickarangType;
import vazkii.quark.content.tools.entity.rang.AbstractPickarang;
import vazkii.quark.content.tools.entity.rang.Echorang;
import vazkii.quark.content.tools.entity.rang.Flamerang;
import vazkii.quark.content.tools.entity.rang.Pickarang;
import vazkii.quark.content.tools.item.PickarangItem;
import vazkii.zeta.client.event.ZClientSetup;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.ZConfigChanged;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;
import vazkii.zeta.util.Hint;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

@ZetaLoadModule(category = "tools")
public class PickarangModule extends ZetaModule {

	@Config(name = "pickarang")
	public static PickarangType<Pickarang> pickarangType = new PickarangType<>(Items.DIAMOND, Items.DIAMOND_PICKAXE, 20, 3, 800, 20.0, 2, 10);

	@Config(name = "flamerang")
	public static PickarangType<Flamerang> flamerangType = new PickarangType<>(Items.NETHERITE_INGOT, Items.NETHERITE_PICKAXE, 20, 4, 1040, 20.0, 3, 10);

	@Config(name = "echorang")
	public static PickarangType<Echorang> echorangType = new PickarangType<Echorang>(Items.ECHO_SHARD, Items.DIAMOND_PICKAXE, 40, 3, 2000, 20.0, 2, 10).canActAsHoe(true);

	@Config(flag = "flamerang")
	public static boolean enableFlamerang = true;

	@Config(flag = "echorang")
	public static boolean enableEchorang = true;

	@Config(description = "Set this to true to use the recipe without the Heart of Diamond, even if the Heart of Diamond is enabled.", flag = "pickarang_never_uses_heart")
	public static boolean neverUseHeartOfDiamond = false;

	@Hint public static Item pickarang;
	@Hint("flamerang") public static Item flamerang;
	@Hint("echorang") public static Item echorang;

	private static List<PickarangType<?>> knownTypes = new ArrayList<>();
	private static boolean isEnabled;

	public static TagKey<Block> pickarangImmuneTag;
	public static TagKey<Block> echorangBreaksAnywayTag;
	public static TagKey<GameEvent> echorangCanListenTag;

	public static QuarkGenericTrigger throwPickarangTrigger;
	public static QuarkGenericTrigger useFlamerangTrigger;

	@LoadEvent
	public final void register(ZRegister event) {
		pickarang = makePickarang(pickarangType, "pickarang", Pickarang::new, Pickarang::new, () -> true);
		flamerang = makePickarang(flamerangType, "flamerang", Flamerang::new, Flamerang::new, () -> enableFlamerang);
		echorang = makePickarang(echorangType, "echorang", Echorang::new, Echorang::new, () -> enableEchorang);

		throwPickarangTrigger = QuarkAdvancementHandler.registerGenericTrigger("throw_pickarang");
		useFlamerangTrigger = QuarkAdvancementHandler.registerGenericTrigger("use_flamerang");
	}

	private <T extends AbstractPickarang<T>> Item makePickarang(PickarangType<T> type, String name,
			EntityType.EntityFactory<T> entityFactory,
			PickarangType.PickarangConstructor<T> thrownFactory,
			BooleanSupplier condition) {

		EntityType<T> entityType = EntityType.Builder.<T>of(entityFactory, MobCategory.MISC)
				.sized(0.4F, 0.4F)
				.clientTrackingRange(4)
				.updateInterval(10)
				.setCustomClientFactory((t, l) -> entityFactory.create(type.getEntityType(), l))
				.build(name);
		Quark.ZETA.registry.register(entityType, name, Registry.ENTITY_TYPE_REGISTRY);

		knownTypes.add(type);
		type.setEntityType(entityType, thrownFactory);
		return new PickarangItem(name, this, propertiesFor(type.durability, type.isFireResistant()), type).setCondition(condition);
	}

	private Item.Properties propertiesFor(int durability, boolean fireResist) {
		Item.Properties properties = new Item.Properties()
				.stacksTo(1)
				.tab(CreativeModeTab.TAB_TOOLS);

		if (durability > 0)
			properties.durability(durability);

		if(fireResist)
			properties.fireResistant();

		return properties;
	}

	@LoadEvent
	public final void setup(ZCommonSetup event) {
		pickarangImmuneTag = BlockTags.create(new ResourceLocation(Quark.MOD_ID, "pickarang_immune"));
		echorangBreaksAnywayTag = BlockTags.create(new ResourceLocation(Quark.MOD_ID, "echorang_breaks_anyway"));
		echorangCanListenTag = TagKey.create(Registry.GAME_EVENT_REGISTRY, new ResourceLocation(Quark.MOD_ID, "echorang_can_listen"));
	}

	@LoadEvent
	public final void clientSetup(ZClientSetup event) {
		knownTypes.forEach(t -> EntityRenderers.register(t.getEntityType(), PickarangRenderer::new));
	}

	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
		// Pass over to a static reference for easier computing the coremod hook
		isEnabled = this.enabled;
	}

	private static final ThreadLocal<AbstractPickarang<?>> ACTIVE_PICKARANG = new ThreadLocal<>();

	public static void setActivePickarang(AbstractPickarang<?> pickarang) {
		ACTIVE_PICKARANG.set(pickarang);
	}

	public static DamageSource createDamageSource(Player player) {
		AbstractPickarang<?> pickarang = ACTIVE_PICKARANG.get();

		if (pickarang == null)
			return null;

		return new IndirectEntityDamageSource("player", pickarang, player).setProjectile();
	}

	public static boolean getIsFireResistant(boolean vanillaVal, Entity entity) {
		if(!isEnabled || vanillaVal)
			return vanillaVal;

		Entity riding = entity.getVehicle();
		if(riding instanceof AbstractPickarang<?> pick)
			return pick.getPickarangType().isFireResistant();

		return false;
	}

}
