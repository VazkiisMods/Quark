package vazkii.quark.misc.item;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import vazkii.arl.interf.IItemColorProvider;
import vazkii.arl.item.ItemMod;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.misc.entity.EntityParrotEgg;

public class ItemParrotEgg extends ItemMod implements IQuarkItem, IItemColorProvider {

	private static final String[] VARIANTS = {
			"red_parrot_egg",
			"blue_parrot_egg",
			"green_parrot_egg",
			"cyan_parrot_egg",
			"grey_parrot_egg"
	};
	
	private static final int[][] COLORS = {
			{ 0xb20200, 0x005eb7 }, // red
			{ 0x0e26cb, 0x04104e }, // blue
			{ 0x9bd901, 0x426000 }, // green
			{ 0x188bb7, 0xfed305 }, // cyan
			{ 0xababab, 0x616161 }  // grey
	};

	public ItemParrotEgg() {
		super("parrot_egg", VARIANTS);
		setCreativeTab(CreativeTabs.MATERIALS);
		setMaxStackSize(16);
	}
	
	@Override
	public IItemColor getItemColor() {
		return (stack, tintIndex) -> COLORS[Math.min(COLORS.length - 1, stack.getItemDamage())][tintIndex];
	}

	@Override
	public String getUniqueModel() {
		return "parrot_egg";
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);

		if(!playerIn.capabilities.isCreativeMode)
			itemstack.shrink(1);

		worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

		if(!worldIn.isRemote) {
			EntityParrotEgg entityegg = new EntityParrotEgg(worldIn, playerIn, itemstack.getItemDamage());
			entityegg.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
			worldIn.spawnEntity(entityegg);
		}

		playerIn.addStat(StatList.getObjectUseStats(this));
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
	}


}
