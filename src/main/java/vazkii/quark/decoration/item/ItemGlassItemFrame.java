package vazkii.quark.decoration.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.arl.interf.IExtraVariantHolder;
import vazkii.arl.item.ItemMod;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.decoration.entity.EntityGlassItemFrame;
import vazkii.quark.decoration.feature.FlatItemFrames;

public class ItemGlassItemFrame extends ItemMod implements IQuarkItem, IExtraVariantHolder {

	private static final String[] EXTRA_VARIANTS = {
			"glass_item_frame_world"
	};
	
	public ItemGlassItemFrame() {
		super("glass_item_frame");
		setCreativeTab(CreativeTabs.DECORATIONS);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = playerIn.getHeldItem(hand);
		BlockPos blockpos = pos.offset(facing);

		if((ModuleLoader.isFeatureEnabled(FlatItemFrames.class) || facing.getAxis() != EnumFacing.Axis.Y) && playerIn.canPlayerEdit(blockpos, facing, stack)) {
			EntityHanging entityhanging = createEntity(worldIn, blockpos, facing);

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

	private EntityHanging createEntity(World worldIn, BlockPos pos, EnumFacing clickedSide) {
		return new EntityGlassItemFrame(worldIn, pos, clickedSide);
	}
	
	@Override
	public String[] getExtraVariants() {
		return EXTRA_VARIANTS;
	}
	
}
