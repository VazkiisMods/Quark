/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [20/03/2016, 23:56:07 (GMT)]
 */
package vazkii.quark.automation.feature;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import vazkii.quark.base.module.Feature;

public class DispensersPlaceSeeds extends Feature {

	@Override
	public void init(FMLInitializationEvent event) {
		BlockDispenser.dispenseBehaviorRegistry.putObject(Items.wheat_seeds, new BehaviourSeeds(Blocks.wheat));
		BlockDispenser.dispenseBehaviorRegistry.putObject(Items.potato, new BehaviourSeeds(Blocks.potatoes));
		BlockDispenser.dispenseBehaviorRegistry.putObject(Items.carrot, new BehaviourSeeds(Blocks.carrots));
		BlockDispenser.dispenseBehaviorRegistry.putObject(Items.nether_wart, new BehaviourSeeds(Blocks.nether_wart));
		BlockDispenser.dispenseBehaviorRegistry.putObject(Items.pumpkin_seeds, new BehaviourSeeds(Blocks.pumpkin_stem));
		BlockDispenser.dispenseBehaviorRegistry.putObject(Items.melon_seeds, new BehaviourSeeds(Blocks.melon_stem));
		BlockDispenser.dispenseBehaviorRegistry.putObject(Items.beetroot_seeds, new BehaviourSeeds(Blocks.beetroots));
		BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(Blocks.chorus_flower), new BehaviourSeeds(Blocks.chorus_flower));
	}

	public class BehaviourSeeds extends BehaviorDefaultDispenseItem {

		Block block;

		public BehaviourSeeds(Block block) {
			this.block = block;
		}

		@Override
		public ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack par2ItemStack) {
			EnumFacing facing = BlockDispenser.getFacing(par1IBlockSource.getBlockMetadata());
			BlockPos pos = par1IBlockSource.getBlockPos().offset(facing);
			World world = par1IBlockSource.getWorld();

			if(world.isAirBlock(pos) && block.canPlaceBlockAt(world, pos)) {
				world.setBlockState(pos, block.getDefaultState());
				par2ItemStack.stackSize--;
				return par2ItemStack;
			}

			return super.dispenseStack(par1IBlockSource, par2ItemStack);
		}

	}

}
