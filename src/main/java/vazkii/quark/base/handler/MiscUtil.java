package vazkii.quark.base.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.commands.arguments.blocks.BlockStateParser.BlockResult;
import net.minecraft.core.*;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent.KeyPressed;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.config.screen.AbstractQScreen;
import vazkii.quark.content.experimental.module.EnchantmentsBegoneModule;
import vazkii.quark.mixin.accessor.AccessorLootTable;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = Quark.MOD_ID)
public class MiscUtil {

	public static final ResourceLocation GENERAL_ICONS = new ResourceLocation(Quark.MOD_ID, "textures/gui/general_icons.png");
	public static final ResourceLocation ATTRIBUTE_ICONS = new ResourceLocation(Quark.MOD_ID, "textures/gui/attribute_icons.png");

	public static final int BASIC_GUI_TEXT_COLOR = 0x404040;

	public static final Direction[] HORIZONTALS = new Direction[] {
			Direction.NORTH,
			Direction.SOUTH,
			Direction.WEST,
			Direction.EAST
	};


	public static BooleanProperty directionProperty(Direction direction) {
		return switch (direction) {
			case DOWN -> BlockStateProperties.DOWN;
			case UP -> BlockStateProperties.UP;
			case NORTH -> BlockStateProperties.NORTH;
			case SOUTH -> BlockStateProperties.SOUTH;
			case WEST -> BlockStateProperties.WEST;
			case EAST -> BlockStateProperties.EAST;
		};
	}

	@OnlyIn(Dist.CLIENT)
	public static void drawChatBubble(PoseStack matrix, int x, int y, Font font, String text, float alpha, boolean extendRight) {
		matrix.pushPose();
		matrix.translate(0, 0, 200);
		RenderSystem.setShaderTexture(0, MiscUtil.GENERAL_ICONS);
		int w = font.width(text);
		int left = x - (extendRight ? 0 : w);
		int top = y - 8;

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);

		if(extendRight) {
			Screen.blit(matrix, left, top, 227, 9, 6, 17, 256, 256);
			for(int i = 0; i < w; i++)
				Screen.blit(matrix, left + i + 6, top, 232, 9, 1, 17, 256, 256);
			Screen.blit(matrix, left + w + 5, top, 236, 9, 5, 17, 256, 256);
		} else {
			Screen.blit(matrix, left, top, 242, 9, 5, 17, 256, 256);
			for(int i = 0; i < w; i++)
				Screen.blit(matrix, left + i + 5, top, 248, 9, 1, 17, 256, 256);
			Screen.blit(matrix, left + w + 5, top, 250, 9, 6, 17, 256, 256);
		}

		int alphaInt = (int) (256F * alpha) << 24;
		font.draw(matrix, text, left + 5, top + 3, alphaInt);
		matrix.popPose();
	}

	public static void addToLootTable(LootTable table, LootPoolEntryContainer entry) {
		List<LootPool> pools = ((AccessorLootTable) table).quark$getPools();
		if (pools != null && !pools.isEmpty()) {
			LootPool firstPool = pools.get(0);
			LootPoolEntryContainer[] entries = firstPool.entries;

			LootPoolEntryContainer[] newEntries = new LootPoolEntryContainer[entries.length + 1];
			System.arraycopy(entries, 0, newEntries, 0, entries.length);

			newEntries[entries.length] = entry;
			firstPool.entries = newEntries;
		}
	}

	public static void damageStack(Player player, InteractionHand hand, ItemStack stack, int dmg) {
		stack.hurtAndBreak(dmg, player, (p) -> p.broadcastBreakEvent(hand));
	}

	public static void initializeEnchantmentList(Iterable<String> enchantNames, List<Enchantment> enchants) {
		enchants.clear();
		for(String s : enchantNames) {
			Enchantment enchant = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(s));
			if (enchant != null && !EnchantmentsBegoneModule.shouldBegone(enchant))
				enchants.add(enchant);
		}
	}

	public static Vec2 getMinecraftAngles(Vec3 direction) {
		// <sin(-y) * cos(p), -sin(-p), cos(-y) * cos(p)>

		direction = direction.normalize();

		double pitch = Math.asin(direction.y);
		double yaw = Math.asin(direction.x / Math.cos(pitch));

		return new Vec2((float) (pitch * 180 / Math.PI), (float) (-yaw * 180 / Math.PI));
	}

	public static boolean isEntityInsideOpaqueBlock(Entity entity) {
		BlockPos pos = entity.blockPosition();
		return !entity.noPhysics && entity.level.getBlockState(pos).isSuffocating(entity.level, pos);
	}

	public static boolean validSpawnLight(ServerLevelAccessor world, BlockPos pos, RandomSource rand) {
		if (world.getBrightness(LightLayer.SKY, pos) > rand.nextInt(32)) {
			return false;
		} else {
			int light = world.getLevel().isThundering() ? world.getMaxLocalRawBrightness(pos, 10) : world.getMaxLocalRawBrightness(pos);
			return light == 0;
		}
	}

	public static boolean validSpawnLocation(@Nonnull EntityType<? extends Mob> type, @Nonnull LevelAccessor world, MobSpawnType reason, BlockPos pos) {
		BlockPos below = pos.below();
		if (reason == MobSpawnType.SPAWNER)
			return true;
		BlockState state = world.getBlockState(below);
		return state.getMaterial() == Material.STONE && state.isValidSpawn(world, below, type);
	}

	public static <T> List<T> massRegistryGet(Collection<String> coll, IForgeRegistry<T> registry) {
		return coll.stream().map(ResourceLocation::new).map(registry::getValue).filter(Objects::nonNull).collect(Collectors.toList());
	}

	public static void syncTE(BlockEntity tile) {
		Packet<ClientGamePacketListener> packet = tile.getUpdatePacket();

		if(packet != null && tile.getLevel() instanceof ServerLevel serverLevel) {
			serverLevel.getChunkSource().chunkMap
			.getPlayers(new ChunkPos(tile.getBlockPos()), false)
			.forEach(e -> e.connection.send(packet));
		}
	}

	public static ItemStack putIntoInv(ItemStack stack, LevelAccessor level, BlockPos blockPos, BlockEntity tile, Direction face, boolean simulate, boolean doSimulation) {
		IItemHandler handler = null;

		if(level != null && blockPos != null && level.getBlockState(blockPos).getBlock() instanceof WorldlyContainerHolder holder) {
			handler = new SidedInvWrapper(holder.getContainer(level.getBlockState(blockPos), level, blockPos), face);
		} else if(tile != null) {
			LazyOptional<IItemHandler> opt = tile.getCapability(ForgeCapabilities.ITEM_HANDLER, face);
			if(opt.isPresent())
				handler = opt.orElse(new ItemStackHandler());
			else if(tile instanceof WorldlyContainer container)
				handler = new SidedInvWrapper(container, face);
			else if(tile instanceof Container container)
				handler = new InvWrapper(container);
		}

		if(handler != null)
			return (simulate && !doSimulation) ? ItemStack.EMPTY : ItemHandlerHelper.insertItem(handler, stack, simulate);

		return stack;
	}

	public static boolean canPutIntoInv(ItemStack stack, LevelAccessor level, BlockPos blockPos, BlockEntity tile, Direction face, boolean doSimulation) {
		return putIntoInv(stack, level, blockPos, tile, face, true, doSimulation).isEmpty();
	}

	public static <T> List<T> getTagValues(RegistryAccess access, TagKey<T> tag) {
		Registry<T> registry = access.registryOrThrow(tag.registry());
		HolderSet<T> holderSet = registry.getTag(tag).orElse(new HolderSet.Named<>(registry, tag));

		return holderSet.stream().map(Holder::value).toList();
	}

	public static String toColorString(int color) {
		String colorString = Integer.toHexString(color);
		int targetLength = colorString.length() > 6 ? 8 : 6;
		String zeroes = colorString.length() < targetLength ? "0".repeat(targetLength - colorString.length()) : "";
		return "#" + zeroes + colorString;
	}

	public static int getAsColor(JsonObject object, String key) {
		if (object.has(key)) {
			return convertToColor(object.get(key), key);
		} else {
			throw new JsonSyntaxException("Missing " + key + ", expected to find an item");
		}
	}

	public static int convertToColor(JsonElement element, String key) {
		if (element.isJsonPrimitive()) {
			JsonPrimitive primitive = element.getAsJsonPrimitive();
			if (primitive.isNumber())
				return primitive.getAsInt();
			else if (primitive.isString()) {
				String s = element.getAsString();
				if (s.matches("#[0-9a-f]{6}")) {
					try {
						return Integer.parseInt(s.substring(1), 16);
					} catch (NumberFormatException e) {
						// NO-OP, should be impossible to reach, but fall through to below if so
					}
				}
			}

		}

		throw new JsonSyntaxException("Expected " + key + " to be a color, was " + GsonHelper.getType(element));
	}

	public static BlockState fromString(String key) {
		try {
			BlockResult result = BlockStateParser.parseForBlock(Registry.BLOCK, new StringReader(key), false);
			BlockState state = result.blockState();
			return state == null ? Blocks.AIR.defaultBlockState() : state;
		} catch (CommandSyntaxException e) {
			return Blocks.AIR.defaultBlockState();
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static int getGuiTextColor(String name) {
		return getGuiTextColor(name, BASIC_GUI_TEXT_COLOR);
	}

	@OnlyIn(Dist.CLIENT)
	public static int getGuiTextColor(String name, int base) {
		int ret = base;

		String hex = I18n.get("quark.gui.color." + name);
		if(hex.matches("#[A-F0-9]{6}"))
			ret = Integer.valueOf(hex.substring(1), 16);
		return ret;
	}

	private static int progress;
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onKeystroke(KeyPressed.Pre event) {
		final String[] ids = new String[] {
				"-FCYE87P5L0","mybsDDymrsc","6a4BWpBJppI","thpTOAS1Vgg","ZNcBZM5SvbY","_qJEoSa3Ie0",
				"RWeyOyY_puQ","VBbeuXW8Nko","LIDe-yTxda0","BVVfMFS3mgc","m5qwcYL8a0o","UkY8HvgvBJ8",
				"4K4b9Z9lSwc","tyInv6RWL0Q","tIWpr3tHzII","AFJPFfnzZ7w","846cjX0ZTrk","XEOCbFJjRw0",
				"GEo5bmUKFvI","b6li05zh3Kg", "_EEo-iE5u_A", "SPYX2y4NzTU"
		};
		final int[] keys = new int[] { 265, 265, 264, 264, 263, 262, 263, 262, 66, 65 };
		if(event.getScreen() instanceof AbstractQScreen) {
			if(keys[progress] == event.getKeyCode()) {
				progress++;

				if(progress >= keys.length) {
					progress = 0;
					Util.getPlatform().openUri("https://www.youtube.com/watch?v=" + ids[new Random().nextInt(ids.length)]);
				}
			} else progress = 0;
		}
	}

	//gets rid of lambdas that could contain references to blockstate properties we might not have
	public static BlockBehaviour.Properties copyPropertySafe(BlockBehaviour blockBehaviour) {
		BlockBehaviour.Properties p = BlockBehaviour.Properties.copy(blockBehaviour);
		p.lightLevel(s -> 0);
		p.offsetType(BlockBehaviour.OffsetType.NONE);
		p.color(blockBehaviour.defaultMaterialColor());
		return p;
	}

}
