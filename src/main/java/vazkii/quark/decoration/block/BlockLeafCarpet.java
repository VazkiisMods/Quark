/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [03/07/2016, 18:54:12 (GMT)]
 */
package vazkii.quark.decoration.block;

import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockLeafCarpet extends BlockMetaVariants implements IBlockColorProvider, IQuarkBlock {

	protected static final AxisAlignedBB CARPET_AABB = new AxisAlignedBB(0, 0, 0, 1, 0.0625, 1);

	public BlockLeafCarpet() {
		super("leaf_carpet", Material.LEAVES, Variants.class);
		setHardness(0.2F);
		setSoundType(SoundType.PLANT);
		setCreativeTab(CreativeTabs.DECORATIONS);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IBlockColor getBlockColor() {
		return new IBlockColor() {

			@Override
			public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
				IBlockState base = ((Variants) state.getValue(getVariantProp())).baseState;
				return Minecraft.getMinecraft().getBlockColors().colorMultiplier(base, worldIn, pos, tintIndex);
			}

		};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IItemColor getItemColor() {
		return new IItemColor() {

			@Override
			public int colorMultiplier(ItemStack stack, int tintIndex) {
				ItemStack baseStack = Variants.class.getEnumConstants()[Math.min(5, stack.getItemDamage())].baseStack;
				return Minecraft.getMinecraft().getItemColors().colorMultiplier(baseStack, tintIndex);
			}

		};
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		return true;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return null;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return CARPET_AABB;
	}

	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
        return p_193383_4_ == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

	public static enum Variants implements EnumBase {
		OAK_LEAF_CARPET(new ItemStack(Blocks.LEAVES, 1, 0), Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK)),
		SPRUCE_LEAF_CARPET(new ItemStack(Blocks.LEAVES, 1, 1), Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.SPRUCE)),
		BIRCH_LEAF_CARPET(new ItemStack(Blocks.LEAVES, 1, 2), Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.BIRCH)),
		JUNGLE_LEAF_CARPET(new ItemStack(Blocks.LEAVES, 1, 3), Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE)),
		ACACIA_LEAF_CARPET(new ItemStack(Blocks.LEAVES2, 1, 0), Blocks.LEAVES2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.ACACIA)),
		DARK_OAK_LEAF_CARPET(new ItemStack(Blocks.LEAVES2, 1, 1), Blocks.LEAVES2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.ACACIA));

		private Variants(ItemStack baseStack, IBlockState baseState) {
			this.baseStack = baseStack;
			this.baseState = baseState;
		}

		public final ItemStack baseStack;
		public final IBlockState baseState;
	}

	@Override
	public IStateMapper getStateMapper() {
		return null;
	}

}