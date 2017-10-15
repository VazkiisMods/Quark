package vazkii.quark.automation.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;

public class PistonsMoveTEs extends Feature {

	private static WeakHashMap<World, Map<BlockPos, TileEntity>> movements = new WeakHashMap();
	private static WeakHashMap<World, List<Pair<BlockPos, TileEntity>>> delayedUpdates = new WeakHashMap();

	public static List<String> renderBlacklist;
	public static List<String> movementBlacklist;
	public static List<String> delayedUpdateList;

	@Override
	public void setupConfig() {
		String[] renderBlacklistArray = loadPropStringList("Tile Entity Render Blacklist", "Some mod blocks with complex renders will break everything if moved. Add them here if you find any.", 
				new String[] { "psi:programmer", "botania:starfield" });
		String[] movementBlacklistArray = loadPropStringList("Tile Entity Movement Blacklist", "Blocks with Tile Entities that pistons should not be able to move.", 
				new String[] { "minecraft:mob_spawner", "integrateddynamics:cable" });
		String[] delayedUpdateListArray = loadPropStringList("Delayed Update List", "List of blocks whose tile entity update should be delayed by one tick after placed to prevent corruption.", 
				new String[] { "minecraft:dispenser", "minecraft:dropper" });
		
		renderBlacklist = new ArrayList(Arrays.asList(renderBlacklistArray));
		movementBlacklist = new ArrayList(Arrays.asList(movementBlacklistArray));
		delayedUpdateList = new ArrayList(Arrays.asList(delayedUpdateListArray));
	}
	
	@SubscribeEvent
	public void onWorldTick(WorldTickEvent event) {
		if(!delayedUpdates.containsKey(event.world) || event.phase == Phase.START)
			return;
		
		List<Pair<BlockPos, TileEntity>> delays = delayedUpdates.get(event.world);
		if(delays.isEmpty())
			return;
		
		for(Pair<BlockPos, TileEntity> delay : delays) {
			event.world.setTileEntity(delay.getLeft(), delay.getRight());
			delay.getRight().updateContainingBlockInfo();
		}
		
		delays.clear();
	}
	
	// This is called from injected code and subsequently flipped, so to make it move, we return false
	public static boolean shouldMoveTE(boolean te, IBlockState state) {
		if(!ModuleLoader.isFeatureEnabled(PistonsMoveTEs.class))
			return te;
		
		// Jukeboxes that are playing can't be moved so the music can be stopped
		if(state.getPropertyKeys().contains(BlockJukebox.HAS_RECORD) && state.getValue(BlockJukebox.HAS_RECORD))
			return true;
		
		return PistonsMoveTEs.movementBlacklist.contains(Block.REGISTRY.getNameForObject(state.getBlock()).toString());
	}
	
	public static void detachTileEntities(World world, BlockPos sourcePos, List<BlockPos> moveList, List<BlockPos> destroyList, EnumFacing facing, boolean extending) {
		if(!ModuleLoader.isFeatureEnabled(PistonsMoveTEs.class))
			return;
		
		for(BlockPos pos : moveList) {
			IBlockState state = world.getBlockState(pos);
			if(state.getBlock().hasTileEntity(state)) {
				TileEntity tile = world.getTileEntity(pos);
				world.removeTileEntity(pos);
				
				registerMovement(world, pos.offset(facing), tile);
			}
		}
	}
	
	public static boolean setPistonBlock(World world, BlockPos pos, IBlockState state, int flags) {
		if(!ModuleLoader.isFeatureEnabled(PistonsMoveTEs.class)) {
			world.setBlockState(pos, state, flags);
			return false;
		}
		
		Block block = state.getBlock();
		TileEntity tile = getAndClearMovement(world, pos);
		boolean destroyed = false;
		
		if(tile != null) {
			IBlockState currState = world.getBlockState(pos);
			TileEntity currTile = world.getTileEntity(pos);
			
			world.setBlockToAir(pos);
			if(!block.canPlaceBlockAt(world, pos)) {
				world.setBlockState(pos, state, flags);
				world.setTileEntity(pos, tile);
				block.dropBlockAsItem(world, pos, state, 0);
				world.setBlockToAir(pos);
				destroyed = true;
			}
			
			if(!destroyed) {
				world.setBlockState(pos, currState);
				world.setTileEntity(pos, currTile);
			}
		}
		
		if(!destroyed) {
			world.setBlockState(pos, state, flags);
			if(tile != null && !world.isRemote) {
				if(delayedUpdateList.contains(Block.REGISTRY.getNameForObject(block).toString()))
					registerDelayedUpdate(world, pos, tile);
				else {
					world.setTileEntity(pos, tile);
					tile.updateContainingBlockInfo();
				}
			}
			world.notifyNeighborsOfStateChange(pos, block, true);
		}
		
		return false; // the value is popped, doesn't matter what we return
	}
	
	private static void registerMovement(World world, BlockPos pos, TileEntity tile) {
		if(!movements.containsKey(world))
			movements.put(world, new HashMap());
		
		movements.get(world).put(pos, tile);
	}
	
	public static TileEntity getMovement(World world, BlockPos pos) {
		return getMovement(world, pos, false);
	}
	
	private static TileEntity getMovement(World world, BlockPos pos, boolean remove) {
		if(!movements.containsKey(world))
			return null;
		
		Map<BlockPos, TileEntity> worldMovements = movements.get(world);
		if(!worldMovements.containsKey(pos))
			return null;
		
		TileEntity ret = worldMovements.get(pos);
		if(remove)
			worldMovements.remove(pos);
		
		return ret; 
	}
	
	private static TileEntity getAndClearMovement(World world, BlockPos pos) {
		TileEntity tile = getMovement(world, pos, true);
		if(tile != null)
			tile.validate();
		
		return tile;
	}
	
	private static void registerDelayedUpdate(World world, BlockPos pos, TileEntity tile) {
		if(!delayedUpdates.containsKey(world))
			delayedUpdates.put(world, new ArrayList());
		
		delayedUpdates.get(world).add(Pair.of(pos, tile));
	}
	
	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
}

