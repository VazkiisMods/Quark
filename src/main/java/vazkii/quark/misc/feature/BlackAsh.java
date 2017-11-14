package vazkii.quark.misc.feature;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.misc.block.BlockBlackAsh;

public class BlackAsh extends Feature {

	public static Block black_ash;
	
	int witherSkeletonMin, witherSkeletonMax, witherMin, witherMax;
	boolean removeCoalDrops;
	
	@Override
	public void setupConfig() {
		witherSkeletonMin = loadPropInt("Wither Skeleton Min Drop", "", 0);
		witherSkeletonMax = loadPropInt("Wither Skeleton Max Drop", "", 3);
		witherMin = loadPropInt("Wither Min Drop", "", 12);
		witherMax = loadPropInt("Wither Max Drop", "", 20);
		removeCoalDrops = loadPropBool("Remove Wither Skeleton Coal Drops", "", true);
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		black_ash = new BlockBlackAsh();
	}
	
	@SubscribeEvent
	public void onDrops(LivingDropsEvent event) {
		Entity e = event.getEntity();

		if(e instanceof EntityWitherSkeleton) {
			if(removeCoalDrops)
				event.getDrops().removeIf((ei) -> ei.getItem().getItem() == Items.COAL);
			
			addDrop(event.getDrops(), e, witherSkeletonMin, witherSkeletonMax);
		} else if(e instanceof EntityWither)
			addDrop(event.getDrops(), e, witherMin, witherMax);
	}
	
	@SubscribeEvent
	public void onSpawnCheck(LivingSpawnEvent.CheckSpawn event) {
		Entity e = event.getEntity();
		World world = event.getWorld();
		BlockPos pos = e.getPosition();
		
		if(world.getBlockState(pos).getBlock() == black_ash || world.getBlockState(pos.down(2)).getBlock() == black_ash)
			event.setResult(Result.DENY);
	}
	
	private void addDrop(List<EntityItem> drops, Entity e, int min, int max) {
		int amount = e.world.rand.nextInt(Math.abs(max - min) + 1) + Math.min(min, max);
		if(amount > 0) 
			drops.add(new EntityItem(e.world, e.posX, e.posY, e.posZ, new ItemStack(black_ash, amount)));
	}
	
	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
