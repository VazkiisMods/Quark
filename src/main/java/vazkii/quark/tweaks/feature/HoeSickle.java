/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * 
 * File Created @ [16/07/2016, 21:39:56 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class HoeSickle extends Feature {

	@SubscribeEvent
	public void onBlockBroken(BlockEvent.BreakEvent event) {
		World world = event.getWorld();
		EntityPlayer player = event.getPlayer();
		BlockPos basePos = event.getPos();
		ItemStack stack = player.getHeldItemMainhand();
		if(!stack.isEmpty() && stack.getItem() instanceof ItemHoe && canHarvest(world, basePos, event.getState())) {
			int range = 1;
			if(stack.getItem() == Items.DIAMOND_HOE)
				range++;
			
			for(int i = -range; i < range + 1; i++)
					for(int k = -range; k < range + 1; k++) {
						if(i == 0 && k == 0)
							continue;
						
						BlockPos pos = basePos.add(i, 0, k);
						IBlockState state = world.getBlockState(pos);
						if(canHarvest(world, pos, state)) {
							Block block = state.getBlock();
							if(block.canHarvestBlock(world, pos, player))
								block.harvestBlock(world, player, pos, state, world.getTileEntity(pos), stack);
							world.setBlockToAir(pos);
							world.playEvent(2001, pos, Block.getIdFromBlock(block) + (block.getMetaFromState(state) << 12));
						}
					}
			
			stack.damageItem(1, player);
		}
	}
	
	private boolean canHarvest(World world, BlockPos pos, IBlockState state) {
		Block block = state.getBlock();
		if(block instanceof IPlantable) {
			IPlantable plant = (IPlantable) block;
			EnumPlantType type = plant.getPlantType(world, pos);
			return type != EnumPlantType.Water && type != EnumPlantType.Desert;
		}
		
		return false;
	}
	
	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
}
