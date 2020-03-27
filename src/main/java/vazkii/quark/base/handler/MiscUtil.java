package vazkii.quark.base.handler;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.base.Predicates;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardKeyPressedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import vazkii.quark.base.Quark;

@EventBusSubscriber(modid = Quark.MOD_ID)
public class MiscUtil {

	public static final ResourceLocation GENERAL_ICONS = new ResourceLocation(Quark.MOD_ID, "textures/gui/general_icons.png");

	public static final Direction[] HORIZONTALS = new Direction[] {
			Direction.NORTH,
			Direction.SOUTH,
			Direction.WEST,
			Direction.EAST
	};

	public static final String[] VARIANT_WOOD_TYPES = new String[] {
			"spruce",
			"birch",
			"jungle",
			"acacia", 
			"dark_oak"	
	};

	public static final String[] ALL_WOOD_TYPES = new String[] {
			"oak",
			"spruce",
			"birch",
			"jungle",
			"acacia", 
			"dark_oak"
	};

	// TODO change to ATs
	public static void addToLootTable(LootTable table, LootEntry entry) {
		List<LootPool> pools = ObfuscationReflectionHelper.getPrivateValue(LootTable.class, table, "field_186466_c"); // table.pools;
		if (pools == null)
			return;
		LootPool pool = pools.get(0);
		List<LootEntry> list = ObfuscationReflectionHelper.getPrivateValue(LootPool.class, pool, "field_186453_a"); // pool.lootEntries;
		if (list == null)
			return;
		list.add(entry);
	}

	public static void damageStack(PlayerEntity player, Hand hand, ItemStack stack, int dmg) {
		stack.damageItem(dmg, player, (p) -> p.sendBreakAnimation(hand));
	}
	
	public static <T, V> void editFinalField(Class<T> clazz, String fieldName, Object obj, V value) {
		Field f = ObfuscationReflectionHelper.findField(clazz, fieldName);
		editFinalField(f, obj, value);
	}

	public static <T> void editFinalField(Field f, Object obj, T value) {
		try {
			f.setAccessible(true);
			
			Field modifiers = Field.class.getDeclaredField("modifiers");
			modifiers.setAccessible(true);
			modifiers.setInt(f, f.getModifiers() & ~Modifier.FINAL);
			
			f.set(obj, value);
		} catch(ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void initializeEnchantmentList(Iterable<String> enchantNames, List<Enchantment> enchants) {
		enchants.clear();
		for(String s : enchantNames) {
			ResourceLocation r = new ResourceLocation(s);
			Enchantment e = ForgeRegistries.ENCHANTMENTS.getValue(r);
			if(e != null)
				enchants.add(e);
		}
	}

	public static Vec2f getMinecraftAngles(Vec3d direction) {
		// <sin(-y) * cos(p), -sin(-p), cos(-y) * cos(p)>

		direction = direction.normalize();

		double pitch = Math.asin(direction.y);
		double yaw = Math.asin(direction.x / Math.cos(pitch));

		return new Vec2f((float) (pitch * 180 / Math.PI), (float) (-yaw * 180 / Math.PI));
	}
	
	public static boolean isEntityInsideOpaqueBlock(Entity entity) {
		BlockPos pos = entity.getPosition();
		return !entity.noClip && entity.world.getBlockState(pos).isSuffocating(entity.world, pos);
	}
	
	public static boolean validSpawnLight(IWorld world, BlockPos pos, Random rand) {
		if (world.getLightFor(LightType.SKY, pos) > rand.nextInt(32)) {
			return false;
		} else {
			int light = world.getWorld().isThundering() ? world.getNeighborAwareLightSubtracted(pos, 10) : world.getLight(pos);
			return light <= rand.nextInt(8);
		}
	}
	
	public static boolean validSpawnLocation(@Nonnull EntityType<? extends MobEntity> type, @Nonnull IWorld world, SpawnReason reason, BlockPos pos) {
		BlockPos below = pos.down();
		if (reason == SpawnReason.SPAWNER)
			return true;
		BlockState state = world.getBlockState(below);
		return state.getMaterial() == Material.ROCK && state.canEntitySpawn(world, below, type);
	}
	
	public static <T extends IForgeRegistryEntry<T>> List<T> massRegistryGet(Collection<String> coll, IForgeRegistry<T> registry) {
		return coll.stream().map(ResourceLocation::new).map(registry::getValue).filter(Predicates.notNull()).collect(Collectors.toList());
	}
	
	private static int progress;
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onKeystroke(KeyboardKeyPressedEvent.Pre event) {
		final String[] ids = new String[] {
			"FCYE87P5L0","mybsDDymrsc","6a4BWpBJppI","thpTOAS1Vgg","ZNcBZM5SvbY","_qJEoSa3Ie0", 
			"RWeyOyY_puQ","VBbeuXW8Nko","LIDe-yTxda0","BVVfMFS3mgc","m5qwcYL8a0o","UkY8HvgvBJ8",
			"4K4b9Z9lSwc","tyInv6RWL0Q","tIWpr3tHzII","AFJPFfnzZ7w","846cjX0ZTrk","XEOCbFJjRw0",
			"8Vto_qUIjcA"
		};
		final int[] keys = new int[] { 265, 265, 264, 264, 263, 262, 263, 262, 66, 65 };
		if(event.getGui() instanceof MainMenuScreen) {
			if(keys[progress] == event.getKeyCode()) {
				progress++;
				
				if(progress >= keys.length) {
					progress = 0;
					Util.getOSType().openURI("https://www.youtube.com/watch?v=" + ids[new Random().nextInt(ids.length)]);
				}
			} else progress = 0;
		}
	}

}
