/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [30/03/2016, 15:37:53 (GMT)]
 */
package vazkii.quark.base.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.handler.RecipeHandler;

public class BlockModWall extends BlockMod {

	public static final PropertyBool UP = PropertyBool.create("up");
	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");

	protected static final AxisAlignedBB[] field_185751_g = new AxisAlignedBB[] {new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.0D, 0.0D, 0.25D, 0.75D, 1.0D, 1.0D), new AxisAlignedBB(0.25D, 0.0D, 0.0D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.3125D, 0.0D, 0.0D, 0.6875D, 0.875D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.75D, 1.0D, 1.0D), new AxisAlignedBB(0.25D, 0.0D, 0.25D, 1.0D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.0D, 0.25D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.3125D, 1.0D, 0.875D, 0.6875D), new AxisAlignedBB(0.0D, 0.0D, 0.25D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.25D, 0.0D, 0.0D, 1.0D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.75D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};
	protected static final AxisAlignedBB[] field_185750_B = new AxisAlignedBB[] {field_185751_g[0].setMaxY(1.5D), field_185751_g[1].setMaxY(1.5D), field_185751_g[2].setMaxY(1.5D), field_185751_g[3].setMaxY(1.5D), field_185751_g[4].setMaxY(1.5D), field_185751_g[5].setMaxY(1.5D), field_185751_g[6].setMaxY(1.5D), field_185751_g[7].setMaxY(1.5D), field_185751_g[8].setMaxY(1.5D), field_185751_g[9].setMaxY(1.5D), field_185751_g[10].setMaxY(1.5D), field_185751_g[11].setMaxY(1.5D), field_185751_g[12].setMaxY(1.5D), field_185751_g[13].setMaxY(1.5D), field_185751_g[14].setMaxY(1.5D), field_185751_g[15].setMaxY(1.5D)};

	public BlockModWall(String name, IBlockState state) {
		super(name, state.getMaterial());

		setHardness(state.getBlockHardness(null, new BlockPos(0, 0, 0)));
		setResistance(state.getBlock().getExplosionResistance(null) * 5F / 3F);
		setStepSound(state.getBlock().getStepSound());
		setDefaultState(blockState.getBaseState().withProperty(UP, Boolean.valueOf(false)).withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false)));
		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		state = getActualState(state, source, pos);
		return field_185751_g[func_185749_i(state)];
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		blockState = getActualState(blockState, worldIn, pos);
		return field_185750_B[func_185749_i(blockState)];
	}

	private static int func_185749_i(IBlockState p_185749_0_) {
		int i = 0;

		if(p_185749_0_.getValue(NORTH).booleanValue())
			i |= 1 << EnumFacing.NORTH.getHorizontalIndex();

		if(p_185749_0_.getValue(EAST).booleanValue())
			i |= 1 << EnumFacing.EAST.getHorizontalIndex();

		if(p_185749_0_.getValue(SOUTH).booleanValue())
			i |= 1 << EnumFacing.SOUTH.getHorizontalIndex();

		if(p_185749_0_.getValue(WEST).booleanValue())
			i |= 1 << EnumFacing.WEST.getHorizontalIndex();

		return i;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	private boolean canConnectTo(IBlockAccess worldIn, BlockPos pos) {
		IBlockState iblockstate = worldIn.getBlockState(pos);
		Block block = iblockstate.getBlock();
		Material material = iblockstate.getMaterial();
		return block == Blocks.barrier ? false : block != this && !(block instanceof BlockFenceGate) ? material.isOpaque() && iblockstate.isFullCube() ? material != Material.gourd : block instanceof BlockModWall ? true : false : true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		for(BlockWall.EnumType blockwall$enumtype : BlockWall.EnumType.values())
			list.add(new ItemStack(itemIn, 1, blockwall$enumtype.getMetadata()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return side == EnumFacing.DOWN ? super.shouldSideBeRendered(blockState, blockAccess, pos, side) : true;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		boolean flag = canConnectTo(worldIn, pos.north());
		boolean flag1 = canConnectTo(worldIn, pos.east());
		boolean flag2 = canConnectTo(worldIn, pos.south());
		boolean flag3 = canConnectTo(worldIn, pos.west());
		boolean flag4 = flag && !flag1 && flag2 && !flag3 || !flag && flag1 && !flag2 && flag3;
		return state.withProperty(UP, Boolean.valueOf(!flag4 || !worldIn.isAirBlock(pos.up()))).withProperty(NORTH, Boolean.valueOf(flag)).withProperty(EAST, Boolean.valueOf(flag1)).withProperty(SOUTH, Boolean.valueOf(flag2)).withProperty(WEST, Boolean.valueOf(flag3));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {UP, NORTH, EAST, WEST, SOUTH});
	}

	@Override
	public IProperty[] getIgnoredProperties() {
		return new IProperty[] {UP, NORTH, EAST, WEST, SOUTH};
	}

	public static void initWall(Block base, int meta, Block block) {
		RecipeHandler.addOreDictRecipe(new ItemStack(block, 6),
				"BBB", "BBB",
				'B', new ItemStack(base, 1, meta));
	}

}
