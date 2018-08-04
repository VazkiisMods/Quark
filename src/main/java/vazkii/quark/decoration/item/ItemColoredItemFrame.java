/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [19/06/2016, 22:34:03 (GMT)]
 */
package vazkii.quark.decoration.item;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.interf.IExtraVariantHolder;
import vazkii.arl.interf.IItemColorProvider;
import vazkii.arl.item.ItemMod;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.decoration.entity.EntityColoredItemFrame;
import vazkii.quark.decoration.feature.FlatItemFrames;


public class ItemColoredItemFrame extends ItemMod implements IItemColorProvider, IExtraVariantHolder, IQuarkItem {

	private static final String[] VARIANTS = {
			"colored_item_frame_white",
			"colored_item_frame_orange",
			"colored_item_frame_magenta",
			"colored_item_frame_light_blue",
			"colored_item_frame_yellow",
			"colored_item_frame_lime",
			"colored_item_frame_pink",
			"colored_item_frame_gray",
			"colored_item_frame_silver",
			"colored_item_frame_cyan",
			"colored_item_frame_purple",
			"colored_item_frame_blue",
			"colored_item_frame_brown",
			"colored_item_frame_green",
			"colored_item_frame_red",
			"colored_item_frame_black"
	};

	private static final String[] EXTRA_VARIANTS = {
			"colored_item_frame_normal",
			"colored_item_frame_wood",
			"colored_item_frame_map",
			"colored_item_frame_map_wood"
	};

	public ItemColoredItemFrame() {
		super("colored_item_frame", VARIANTS);
		setCreativeTab(CreativeTabs.DECORATIONS);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = playerIn.getHeldItem(hand);
		BlockPos blockpos = pos.offset(facing);

		if((ModuleLoader.isFeatureEnabled(FlatItemFrames.class) || facing.getAxis() != EnumFacing.Axis.Y) && playerIn.canPlayerEdit(blockpos, facing, stack)) {
			EntityHanging entityhanging = createEntity(worldIn, blockpos, facing, stack.getItemDamage());

			if(entityhanging != null && entityhanging.onValidSurface()) {
				if(!worldIn.isRemote) {
					entityhanging.playPlaceSound();
					worldIn.spawnEntity(entityhanging);
				}

				stack.shrink(1);
			}

			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.FAIL;
	}

	private EntityHanging createEntity(World worldIn, BlockPos pos, EnumFacing clickedSide, int color) {
		return new EntityColoredItemFrame(worldIn, pos, clickedSide, color);
	}

	@Override
	public String getUniqueModel() {
		return "colored_item_frame";
	}

	@Override
	public String[] getExtraVariants() {
		return EXTRA_VARIANTS;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IItemColor getItemColor() {
		return new IItemColor() {

			@Override
			public int colorMultiplier(ItemStack stack, int tintIndex) {
				return tintIndex == 1 ? ItemDye.DYE_COLORS[15 - Math.min(15, stack.getItemDamage())] : 0xFFFFFF;
			}
		};
	}

}
