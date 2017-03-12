package vazkii.quark.automation.feature;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.misc.feature.LockDirectionHotkey;

public class DispensersPlaceBlocks extends Feature {

	List<String> blacklist;
	
	@Override
	public void setupConfig() {
		String[] blacklistArray = loadPropStringList("Blacklist", "Blocks that dispensers should not be able to place", new String[] {
				"minecraft:water",
				"minecraft:flowing_water",
				"minecraft:lava",
				"minecraft:flowing_lava",
				"minecraft:fire"
		});
		
		blacklist = Arrays.asList(blacklistArray);
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		for(ResourceLocation r : Block.REGISTRY.getKeys()) {
			Block block = Block.REGISTRY.getObject(r);
			Item item = Item.getItemFromBlock(block);

			if(block == null || item == null || !(item instanceof ItemBlock) || blacklist.contains(r.toString()))
				continue;

			if(!BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.containsKey(item))
				BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(item, new BehaviourBlock((ItemBlock) item, block));
		}
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
	@Override
	public String[] getIncompatibleMods() {
		return new String[] { "blockdispenser" };
	} 
	
	public class BehaviourBlock extends BehaviorDefaultDispenseItem {

		ItemBlock item;
		Block block;

		public BehaviourBlock(ItemBlock item, Block block) {
			this.item = item;
			this.block = block;
		}

		@Override
		public ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack par2ItemStack) {
			EnumFacing facing = par1IBlockSource.getBlockState().getValue(BlockDispenser.FACING);
			Axis axis = facing.getAxis();

			BlockPos pos = par1IBlockSource.getBlockPos().offset(facing);
			World world = par1IBlockSource.getWorld();

			if(world.isAirBlock(pos) && block.canPlaceBlockAt(world, pos)) {
				int meta = item.getMetadata(par2ItemStack.getItemDamage());
				IBlockState state = block.getStateFromMeta(meta);

				LockDirectionHotkey.setBlockRotated(world, state, pos, facing);
				
				SoundType soundtype = block.getSoundType();
				world.playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				par2ItemStack.shrink(1);
				return par2ItemStack;
			}

			return super.dispenseStack(par1IBlockSource, par2ItemStack);
		}

	}
	
}
