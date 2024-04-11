package org.violetmoon.quark.content.tools.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.saveddata.maps.MapDecoration.Type;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.Nullable;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.QuarkClient;
import org.violetmoon.quark.content.mobs.module.StonelingsModule;
import org.violetmoon.quark.content.tools.module.PathfinderMapsModule;
import org.violetmoon.quark.content.tools.module.PathfinderMapsModule.TradeInfo;
import org.violetmoon.quark.content.world.module.GlimmeringWealdModule;
import org.violetmoon.zeta.item.ZetaItem;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.CreativeTabManager;
import org.violetmoon.zeta.util.ItemNBTHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PathfindersQuillItem extends ZetaItem implements CreativeTabManager.AppendsUniquely {

	private static final Direction[] DIRECTIONS = new Direction[] { Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH };

	public static final String TAG_BIOME = "targetBiome";
	public static final String TAG_COLOR = "targetBiomeColor";
	public static final String TAG_UNDERGROUND = "targetBiomeUnderground";

	protected static final String TAG_IS_SEARCHING = "isSearchingForBiome";
	protected static final String TAG_SOURCE_X = "searchSourceX";
	protected static final String TAG_SOURCE_Z = "searchSourceZ";
	protected static final String TAG_POS_X = "searchPosX";
	protected static final String TAG_POS_Z = "searchPosZ";
	protected static final String TAG_POS_LEG = "searchPosLeg";
	protected static final String TAG_POS_LEG_INDEX = "searchPosLegIndex";

	public PathfindersQuillItem(ZetaModule module, Item.Properties properties) {
		super("pathfinders_quill", module, properties);
		CreativeTabManager.addToCreativeTabNextTo(CreativeModeTabs.TOOLS_AND_UTILITIES, this, Items.MAP, false);
	}

	public PathfindersQuillItem(ZetaModule module) {
		this(module, new Item.Properties().stacksTo(1));
	}

	public static ResourceLocation getTargetBiome(ItemStack stack) {
		String str = ItemNBTHelper.getString(stack, TAG_BIOME, "");
		if(str.isEmpty())
			return null;

		return new ResourceLocation(str);
	}

	public static int getOverlayColor(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, TAG_COLOR, 0xFFFFFF);
	}

	public static ItemStack forBiome(String biome, int color) {
		ItemStack stack = new ItemStack(PathfinderMapsModule.pathfinders_quill);
		setBiome(stack, biome, color, false);
		return stack;
	}

	public static void setBiome(ItemStack stack, String biome, int color, boolean underground) {
		ItemNBTHelper.setString(stack, TAG_BIOME, biome);
		ItemNBTHelper.setInt(stack, TAG_COLOR, color);
		ItemNBTHelper.setBoolean(stack, TAG_UNDERGROUND, underground);
	}

	public static @Nullable ItemStack getActiveQuill(Player player) {
		for(ItemStack stack : player.getInventory().items)
			if(stack.getItem() instanceof PathfindersQuillItem) {
				boolean searching = ItemNBTHelper.getBoolean(stack, TAG_IS_SEARCHING, false);

				if(searching)
					return stack;
			}

		for(ItemStack stack : player.getInventory().offhand)
			if(stack.getItem() instanceof PathfindersQuillItem) {
				boolean searching = ItemNBTHelper.getBoolean(stack, TAG_IS_SEARCHING, false);

				if(searching)
					return stack;
			}

		for(ItemStack stack : player.getInventory().armor)
			if(stack.getItem() instanceof PathfindersQuillItem) {
				boolean searching = ItemNBTHelper.getBoolean(stack, TAG_IS_SEARCHING, false);

				if(searching)
					return stack;
			}

		return null;
	}

	@Override
	public boolean shouldCauseReequipAnimationZeta(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || (oldStack.getItem() != newStack.getItem());
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if(this.getTarget(stack) == null)
			return InteractionResultHolder.pass(stack);

		ItemStack active = getActiveQuill(player);
		if(active != null) {
			player.displayClientMessage(Component.translatable("quark.misc.only_one_quill"), true);
			return InteractionResultHolder.fail(stack);
		}

		Vec3 pos = player.getPosition(1F);
		level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 0.5F, 1F);

		ItemNBTHelper.setBoolean(stack, TAG_IS_SEARCHING, true);
		ItemNBTHelper.setInt(stack, TAG_SOURCE_X, player.getBlockX());
		ItemNBTHelper.setInt(stack, TAG_SOURCE_Z, player.getBlockZ());
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
	}

	public ResourceLocation getTarget(ItemStack stack) {
		return getTargetBiome(stack);
	}

	protected int getIterations() {
		return PathfinderMapsModule.pathfindersQuillSpeed;
	}

	protected boolean isMultiThreaded() {
		return PathfinderMapsModule.multiThreaded;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean held) {
		if(!level.isClientSide
				&& level instanceof ServerLevel sl
				&& ItemNBTHelper.getBoolean(stack, TAG_IS_SEARCHING, false)
				&& entity instanceof Player player
				&& getActiveQuill(player) == stack) {

			ItemStack runningStack = search(stack, sl, player, slot);

			if(runningStack != stack) {
				String msg;

				if(runningStack.isEmpty()) {
					if(PathfinderMapsModule.allowRetrying) {
						runningStack = this.resetSearchingTags(stack);
						msg = getRetryMessage();
					} else
						msg = getFailMessage();
				} else
					msg = getSuccessMessage();

				player.displayClientMessage(Component.translatable(msg), true);

				Vec3 pos = player.getPosition(1F);
				level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.NOTE_BLOCK_CHIME.get(), SoundSource.PLAYERS, 0.5F, 1F);

				player.getInventory().setItem(slot, runningStack);
			}
		}
	}

	protected ItemStack resetSearchingTags(ItemStack stack) {
		stack.removeTagKey(TAG_SOURCE_X);
		stack.removeTagKey(TAG_SOURCE_Z);
		stack.removeTagKey(TAG_IS_SEARCHING);
		stack.removeTagKey(TAG_POS_Z);
		stack.removeTagKey(TAG_POS_Z);
		stack.removeTagKey(TAG_POS_LEG);
		stack.removeTagKey(TAG_POS_LEG_INDEX);
		return stack;
	}

	protected String getRetryMessage() {
		return "quark.misc.quill_retry";
	}

	protected String getSuccessMessage() {
		return "quark.misc.quill_finished";
	}

	protected String getFailMessage() {
		return "quark.misc.quill_failed";
	}

	protected ItemStack search(ItemStack stack, ServerLevel level, Player player, int slot) {
		ResourceLocation searchKey = this.getTarget(stack);
		if(searchKey == null)
			return ItemStack.EMPTY;

		InteractionResultHolder<BlockPos> result;
		if(isMultiThreaded()) {
			result = this.searchConcurrent(searchKey, stack, level, player);
		} else {
			result = this.searchIterative(searchKey, stack, level, player, getIterations());
		}

		if(result.getResult() == InteractionResult.FAIL) {
			return ItemStack.EMPTY;
		} else if(result.getResult() == InteractionResult.PASS) {
			return stack;
		} else {
			BlockPos found = result.getObject();
			return this.createMap(level, found, searchKey, stack);
		}
	}

	protected InteractionResultHolder<BlockPos> searchConcurrent(
			ResourceLocation searchKey, ItemStack stack, ServerLevel level, Player player) {
		int sourceX = ItemNBTHelper.getInt(stack, TAG_SOURCE_X, 0);
		int sourceZ = ItemNBTHelper.getInt(stack, TAG_SOURCE_Z, 0);
		BlockPos centerPos = new BlockPos(sourceX, 64, sourceZ);
		Key key = new Key(GlobalPos.of(level.dimension(), centerPos), searchKey);
		if(COMPUTING.contains(key)) {
			return InteractionResultHolder.pass(BlockPos.ZERO);
		} else if(RESULTS.containsKey(key)) {
			//we could use remove here instead but this serves as a cache since the result is always the same
			var ret = RESULTS.get(key);
			//EXECUTORS.submit(() -> RESULTS.remove(key)); //lmao. no lag spikes allowed. write is slow
			if(ret.getResult() == InteractionResult.PASS) {
				//this should never happen
				return InteractionResultHolder.fail(BlockPos.ZERO);
			}
			return ret;
		} else {
			//we don't want to alter the original stack here
			ItemStack dummy = stack.copy();
			EXECUTORS.submit(() -> {
				COMPUTING.add(key);
				RESULTS.put(key, searchIterative(searchKey, dummy, level, player, Integer.MAX_VALUE));
				COMPUTING.remove(key);
			});
			return InteractionResultHolder.pass(BlockPos.ZERO);
		}
	}

	//basically the old search method
	//pass = not done
	//fail = failed
	//present = success
	protected InteractionResultHolder<BlockPos> searchIterative(
			ResourceLocation searchKey, ItemStack stack, ServerLevel level, Player player, int maxIter) {
		int y = player.getBlockY();
		for(int i = 0; i < maxIter; i++) {

			final int height = 64;

			BlockPos nextPos = nextPos(stack);
			if(nextPos == null) {
				return InteractionResultHolder.fail(BlockPos.ZERO);
			}
			int[] searchedHeights = Mth.outFromOrigin(y, level.getMinBuildHeight() + 1, level.getMaxBuildHeight(), height).toArray();

			int testX = nextPos.getX();
			int testZ = nextPos.getZ();
			int quartX = QuartPos.fromBlock(testX);
			int quartZ = QuartPos.fromBlock(testZ);

			for(int testY : searchedHeights) {
				int quartY = QuartPos.fromBlock(testY);

				ServerChunkCache cache = level.getChunkSource();
				BiomeSource source = cache.getGenerator().getBiomeSource();
				Climate.Sampler sampler = cache.randomState().sampler();

				Holder<Biome> holder = source.getNoiseBiome(quartX, quartY, quartZ, sampler);

				if(holder.is(searchKey)) {
					BlockPos mapPos = new BlockPos(testX, testY, testZ);
					return InteractionResultHolder.sidedSuccess(mapPos, level.isClientSide);
				}
			}
		}
		return InteractionResultHolder.pass(BlockPos.ZERO);
	}

	protected static BlockPos nextPos(ItemStack stack) {
		final int step = 32;

		int sourceX = ItemNBTHelper.getInt(stack, TAG_SOURCE_X, 0);
		int sourceZ = ItemNBTHelper.getInt(stack, TAG_SOURCE_Z, 0);

		int x = ItemNBTHelper.getInt(stack, TAG_POS_X, 0);
		int z = ItemNBTHelper.getInt(stack, TAG_POS_Z, 0);
		int leg = ItemNBTHelper.getInt(stack, TAG_POS_LEG, -1);
		int legIndex = ItemNBTHelper.getInt(stack, TAG_POS_LEG_INDEX, 0);

		BlockPos cursor = new BlockPos(x, 0, z).relative(DIRECTIONS[(leg + 4) % 4]);

		int newX = cursor.getX();
		int newZ = cursor.getZ();

		int legSize = leg / 2 + 1;
		int maxLegs = 4 * Math.floorDiv(PathfinderMapsModule.searchRadius, step);

		if(legIndex >= legSize) {
			if(leg > maxLegs)
				return null;

			leg++;
			legIndex = 0;
		}

		legIndex++;

		ItemNBTHelper.setInt(stack, TAG_POS_X, newX);
		ItemNBTHelper.setInt(stack, TAG_POS_Z, newZ);
		ItemNBTHelper.setInt(stack, TAG_POS_LEG, leg);
		ItemNBTHelper.setInt(stack, TAG_POS_LEG_INDEX, legIndex);

		int retX = sourceX + newX * step;
		int retZ = sourceZ + newZ * step;

		return new BlockPos(retX, 0, retZ);
	}

	public ItemStack createMap(ServerLevel level, BlockPos targetPos, ResourceLocation target, ItemStack original) {
		int color = getOverlayColor(original);
		Component biomeComponent = Component.translatable("biome." + target.getNamespace() + "." + target.getPath());

		ItemStack stack = MapItem.create(level, targetPos.getX(), targetPos.getZ(), (byte) 2, true, true);

		MapItem.renderBiomePreviewMap(level, stack);
		MapItemSavedData.addTargetDecoration(stack, targetPos, "+", Type.RED_X);
		stack.setHoverName(Component.translatable("item.quark.biome_map", biomeComponent));

		stack.getOrCreateTagElement("display").putInt("MapColor", color);
		ItemNBTHelper.setBoolean(stack, PathfinderMapsModule.TAG_IS_PATHFINDER, true);

		return stack;
	}

	@OnlyIn(Dist.CLIENT)
	public static MutableComponent getSearchingComponent() {
		MutableComponent comp = Component.translatable("quark.misc.quill_searching");

		int dots = ((QuarkClient.ticker.ticksInGame / 10) % 4);
		for(int i = 0; i < dots; i++)
			comp.append(".");

		return comp;
	}

	@Override
	public List<ItemStack> appendItemsToCreativeTab() {
		List<ItemStack> items = new ArrayList<>();
		boolean generatedWeald = false;

		for(TradeInfo trade : PathfinderMapsModule.tradeList) {
			if(trade.biome.equals(GlimmeringWealdModule.BIOME_NAME))
				generatedWeald = true;

			items.add(forBiome(trade.biome.toString(), trade.color));
		}

		if(!generatedWeald &&
				Quark.ZETA.modules.isEnabled(StonelingsModule.class) &&
				Quark.ZETA.modules.isEnabled(GlimmeringWealdModule.class) &&
				StonelingsModule.wealdPathfinderMaps) {

			items.add(forBiome(GlimmeringWealdModule.BIOME_NAME.toString(), 0x317546));
		}

		return items;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, Level level, List<Component> comps, TooltipFlag flags) {
		ResourceLocation biome = this.getTarget(stack);
		if(biome != null) {
			if(ItemNBTHelper.getBoolean(stack, TAG_IS_SEARCHING, false))
				comps.add(getSearchingComponent().withStyle(ChatFormatting.BLUE));

			comps.add(Component.translatable("biome." + biome.getNamespace() + "." + biome.getPath()).withStyle(ChatFormatting.GRAY));
		} else
			comps.add(Component.translatable("quark.misc.quill_blank").withStyle(ChatFormatting.GRAY));
	}

	//new concurrent search stuff. Experimental
	private record Key(GlobalPos pos, ResourceLocation structure) {
	}

	private static final Map<Key, InteractionResultHolder<BlockPos>> RESULTS = new ConcurrentHashMap<>();
	private static final Set<Key> COMPUTING = ConcurrentHashMap.newKeySet();
	protected static final ExecutorService EXECUTORS = Executors.newCachedThreadPool();

}
