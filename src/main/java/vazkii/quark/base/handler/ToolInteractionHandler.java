package vazkii.quark.base.handler;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;
import vazkii.quark.base.handler.advancement.QuarkAdvancementHandler;
import vazkii.quark.base.handler.advancement.mod.WaxModifier;
import vazkii.quark.base.module.QuarkModule;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.bus.LoadEvent;

import java.util.*;

public final class ToolInteractionHandler {

	private static final Map<Block, Block> cleanToWaxMap = HashBiMap.create();
	private static final Map<ToolAction, Map<Block, Block>> interactionMaps = new HashMap<>();

	private static final Multimap<QuarkModule, Pair<Block, Block>> waxingByModule = HashMultimap.create();

	public static void registerWaxedBlock(QuarkModule module, Block clean, Block waxed) {
		cleanToWaxMap.put(clean, waxed);
		registerInteraction(ToolActions.AXE_WAX_OFF, waxed, clean);

		waxingByModule.put(module, Pair.of(clean, waxed));
	}

	public static void registerInteraction(ToolAction action, Block in, Block out) {
		if(!interactionMaps.containsKey(action))
			interactionMaps.put(action, new HashMap<>());

		Map<Block, Block> map = interactionMaps.get(action);
		map.put(in, out);
	}

	@LoadEvent
	public static void addModifiers(ZCommonSetup event) {
		for(QuarkModule module : waxingByModule.keySet()) {
			Collection<Pair<Block, Block>> pairs = waxingByModule.get(module);
			Set<Block> unwaxed = new HashSet<>();
			Set<Block> waxed = new HashSet<>();

			for(Pair<Block, Block> pair : pairs) {
				unwaxed.add(pair.getLeft());
				waxed.add(pair.getRight());
			}

			QuarkAdvancementHandler.addModifier(new WaxModifier(module, unwaxed, waxed));
		}
	}

	@SubscribeEvent
	public static void toolActionEvent(BlockEvent.BlockToolModificationEvent event) {
		ToolAction action = event.getToolAction();

		if(interactionMaps.containsKey(action)) {
			Map<Block, Block> map = interactionMaps.get(action);
			BlockState state = event.getState();
			Block block = state.getBlock();

			if(map.containsKey(block)) {
				Block finalBlock = map.get(block);
				event.setFinalState(copyState(state, finalBlock));
			}
		}
	}

	@SubscribeEvent
	public static void itemUse(PlayerInteractEvent.RightClickBlock event) {
		ItemStack stack = event.getItemStack();

		if(stack.getItem() == Items.HONEYCOMB) {
			BlockPos pos = event.getPos();
			Level world = event.getLevel();
			BlockState state = world.getBlockState(pos);
			Block block = state.getBlock();

			if(cleanToWaxMap.containsKey(block)) {
				Block alternate = cleanToWaxMap.get(block);

				if(event.getEntity() instanceof ServerPlayer sp)
					CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(sp, pos, stack);

				if(!world.isClientSide)
					world.setBlockAndUpdate(pos, copyState(state, alternate));
				world.levelEvent(event.getEntity(), LevelEvent.PARTICLES_AND_SOUND_WAX_ON, pos, 0);

				if(!event.getEntity().getAbilities().instabuild)
					stack.setCount(stack.getCount() - 1);

				event.setCanceled(true);
				event.setCancellationResult(InteractionResult.SUCCESS);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static BlockState copyState(BlockState original, Block newBlock) {
		BlockState retState = newBlock.defaultBlockState();
		for(Property prop : original.getProperties())
			if(retState.hasProperty(prop))
				retState = retState.setValue(prop, original.getValue(prop));

		return retState;

	}

}
