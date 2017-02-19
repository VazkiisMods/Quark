/**
 * This class was created by <ichttt>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [19/02/2017, 14:24:42 (CET)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.block.BlockSponge;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class SpongeDriesInNether extends Feature {

	@SubscribeEvent
	public void onItemPlaced(BlockEvent.PlaceEvent event) {
		if (event.getPlacedBlock().equals(Blocks.SPONGE.getDefaultState().withProperty(BlockSponge.WET, true)) &&
				BiomeDictionary.getTypes(event.getWorld().getBiome(event.getPos())).contains(BiomeDictionary.Type.NETHER)) {
			World world = event.getWorld();
			world.playSound(null, event.getPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 2.4F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.9F);
			world.setBlockState(event.getPos(), Blocks.SPONGE.getDefaultState().withProperty(BlockSponge.WET, false));
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}
}
