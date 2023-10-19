package vazkii.quark.addons.oddities.module;

import java.util.Collection;
import java.util.Objects;

import com.mojang.datafixers.util.Pair;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.addons.oddities.client.render.entity.TotemOfHoldingRenderer;
import vazkii.quark.addons.oddities.entity.TotemOfHoldingEntity;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.client.ZAddModels;
import vazkii.zeta.event.client.ZClientSetup;

/**
 * @author WireSegal
 * Created at 1:21 PM on 3/30/20.
 */
@LoadModule(category = "oddities", hasSubscriptions = true)
public class TotemOfHoldingModule extends QuarkModule {
	private static final String TAG_LAST_TOTEM = "quark:lastTotemOfHolding";

	private static final String TAG_DEATH_X = "quark:deathX";
	private static final String TAG_DEATH_Z = "quark:deathZ";
	private static final String TAG_DEATH_DIM = "quark:deathDim";

	public static EntityType<TotemOfHoldingEntity> totemType;

	@Config(description = "Set this to false to remove the behaviour where totems destroy themselves if the player dies again.")
	public static boolean darkSoulsMode = true;

	@Config(name = "Spawn Totem on PVP Kill", description = "Totem will always spawn if the player killer is himself.")
	public static boolean enableOnPK = false;

	@Config(description = "Set this to true to make it so that if a totem is destroyed, the items it holds are destroyed alongside it rather than dropped")
	public static boolean destroyLostItems = false;

	@Config(description = "Set this to false to only allow the owner of a totem to collect its items rather than any player")
	public static boolean allowAnyoneToCollect = true;

	@Config(flag = "soul_compass")
	public static boolean enableSoulCompass = true;

	@LoadEvent
	public final void register(ZRegister event) {
		totemType = EntityType.Builder.of(TotemOfHoldingEntity::new, MobCategory.MISC)
				.sized(0.5F, 1F)
				.updateInterval(128) // update interval
				.fireImmune()
				.setShouldReceiveVelocityUpdates(false)
				.setCustomClientFactory((spawnEntity, world) -> new TotemOfHoldingEntity(totemType, world))
				.build("totem");
		Quark.ZETA.registry.register(totemType, "totem", Registry.ENTITY_TYPE_REGISTRY);
	}

	@LoadEvent
	public final void clientSetup(ZClientSetup event) {
		EntityRenderers.register(totemType, TotemOfHoldingRenderer::new);
	}

	@LoadEvent
	@OnlyIn(Dist.CLIENT)
	public void registerAdditionalModels(ZAddModels event) {
		event.register(new ModelResourceLocation(Quark.MOD_ID, "extra/totem_of_holding", "inventory"));
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerDrops(LivingDropsEvent event) {
		LivingEntity entity = event.getEntity();
		if (!(entity instanceof Player player))
			return;

		Collection<ItemEntity> drops = event.getDrops();

		if(!event.isCanceled() && (enableOnPK || !(event.getSource().getEntity() instanceof Player) || entity == event.getSource().getEntity())) {
			CompoundTag data = player.getPersistentData();
			CompoundTag persistent = data.getCompound(Player.PERSISTED_NBT_TAG);

			if(!drops.isEmpty()) {
				TotemOfHoldingEntity totem = new TotemOfHoldingEntity(totemType, player.level);
				totem.setPos(player.getX(), Math.max(player.level.getMinBuildHeight() + 3, player.getY() + 1), player.getZ());
				totem.setOwner(player);
				totem.setCustomName(player.getDisplayName());
				drops.stream()
				.filter(Objects::nonNull)
				.map(ItemEntity::getItem)
				.filter(stack -> !stack.isEmpty())
				.forEach(totem::addItem);
				if (!player.level.isClientSide)
					player.level.addFreshEntity(totem);

				persistent.putString(TAG_LAST_TOTEM, totem.getUUID().toString());

				event.setCanceled(true);
			} else persistent.putString(TAG_LAST_TOTEM, "");

			BlockPos pos = player.blockPosition(); // getPosition
			persistent.putInt(TAG_DEATH_X, pos.getX());
			persistent.putInt(TAG_DEATH_Z, pos.getZ());
			persistent.putString(TAG_DEATH_DIM, player.level.dimension().location().toString());

			if(!data.contains(Player.PERSISTED_NBT_TAG))
				data.put(Player.PERSISTED_NBT_TAG, persistent);
		}
	}

	public static String getTotemUUID(Player player) {
		CompoundTag cmp = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
		if(cmp.contains(TAG_LAST_TOTEM))
			return cmp.getString(TAG_LAST_TOTEM);

		return "";
	}

	public static Pair<BlockPos, String> getPlayerDeathPosition(Entity e) {
		if(e instanceof Player) {
			CompoundTag cmp = e.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
			if(cmp.contains(TAG_LAST_TOTEM)) {
				int x = cmp.getInt(TAG_DEATH_X);
				int z = cmp.getInt(TAG_DEATH_Z);
				String dim = cmp.getString(TAG_DEATH_DIM);
				return Pair.of(new BlockPos(x, -1, z), dim);
			}
		}

		return null;
	}
}
