/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [05/06/2016, 22:57:00 (GMT)]
 */
package vazkii.quark.misc.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.arl.item.ItemMod;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.item.IQuarkItem;

public class ItemSlimeBucket extends ItemMod implements IQuarkItem {

	public static final String TAG_ENTITY_DATA = "slime_nbt";
	
	public static final String[] VARIANTS = new String[] {
			"slime_bucket_normal",
			"slime_bucket_excited"
	};

	public ItemSlimeBucket() {
		super("slime_bucket", VARIANTS);
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.MISC);
		setContainerItem(Items.BUCKET);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if(isInCreativeTab(tab))
			subItems.add(new ItemStack(this));
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int something, boolean somethingelse) {
		if(!world.isRemote) {
			int x = MathHelper.floor(entity.posX);
			int z = MathHelper.floor(entity.posZ);
			boolean slime = isSlimeChunk(world, x, z);
			int meta = stack.getItemDamage();
			int newMeta = slime ? 1 : 0;
			if(meta != newMeta)
				stack.setItemDamage(newMeta);
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		double x = pos.getX() + 0.5 + facing.getFrontOffsetX();
		double y = pos.getY() + 0.5 + facing.getFrontOffsetY();
		double z = pos.getZ() + 0.5 + facing.getFrontOffsetZ();

		if(!worldIn.isRemote) {
			EntitySlime slime = new EntitySlime(worldIn);
			
			NBTTagCompound data = ItemNBTHelper.getCompound(playerIn.getHeldItem(hand), TAG_ENTITY_DATA, true);
			if(data != null)
				slime.readFromNBT(data);
			else {
				slime.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1.0);
				slime.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
				slime.setHealth(slime.getMaxHealth());
			}
				
			slime.setPosition(x, y, z);

			worldIn.spawnEntity(slime);
			playerIn.swingArm(hand);
		}

		playerIn.setHeldItem(hand, new ItemStack(Items.BUCKET));
		return EnumActionResult.SUCCESS;
	}
	

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		if(stack.hasTagCompound()) {
			NBTTagCompound cmp = ItemNBTHelper.getCompound(stack, TAG_ENTITY_DATA, false);
			if(cmp != null && cmp.hasKey("CustomName")) 
				return I18n.format("item.quark:slime_bucket_named.name", cmp.getString("CustomName")); 
		}
		
		return super.getItemStackDisplayName(stack);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}

	public static boolean isSlimeChunk(World world, int x, int z) {
		Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
		return chunk.getRandomWithSeed(987234911L).nextInt(10) == 0;
	}

}
