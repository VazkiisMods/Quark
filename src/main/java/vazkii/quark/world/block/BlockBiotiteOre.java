/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 * 
 * File Created @ [18/04/2016, 17:30:59 (GMT)]
 */
package vazkii.quark.world.block;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import vazkii.quark.base.block.BlockMod;
import vazkii.quark.world.feature.Biotite;

public class BlockBiotiteOre extends BlockMod {

	public BlockBiotiteOre() {
		super("biotite_ore", Material.rock);
		setHardness(3.2F);
		setResistance(15.0F);
		setStepSound(SoundType.STONE);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Biotite.biotite;
	}

	@Override
	public int quantityDroppedWithBonus(int fortune, Random random) {
		if(fortune > 0) {
			int i = random.nextInt(fortune + 2) - 1;

			if(i < 0)
				i = 0;

			return this.quantityDropped(random) * (i + 1);
		}

		return this.quantityDropped(random);
	}

	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
		super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
	}

	@Override
	public int getExpDrop(IBlockState state, net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune) {
		Random rand = world instanceof World ? ((World)world).rand : new Random();
		return MathHelper.getRandomIntegerInRange(rand, 2, 5);
	}

}
