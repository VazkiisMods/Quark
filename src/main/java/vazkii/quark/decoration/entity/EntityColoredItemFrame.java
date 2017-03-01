/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [19/06/2016, 23:52:04 (GMT)]
 */
package vazkii.quark.decoration.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import vazkii.quark.decoration.feature.ColoredItemFrames;

public class EntityColoredItemFrame extends EntityItemFrame implements IEntityAdditionalSpawnData {

	private static final DataParameter<Integer> COLOR = EntityDataManager.<Integer>createKey(EntityColoredItemFrame.class, DataSerializers.VARINT);
	private static final String TAG_COLOR = "DyeColor";
	private static final String TAG_ITEMDROPCHANCE = "ItemDropChance";
	private float itemDropChance = 1.0F;

	public EntityColoredItemFrame(World worldIn) {
		super(worldIn);
	}

	public EntityColoredItemFrame(World worldIn, BlockPos p_i45852_2_, EnumFacing p_i45852_3_, int color) {
		super(worldIn, p_i45852_2_, p_i45852_3_);
		dataManager.set(COLOR, color);
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		dataManager.register(COLOR, 0);
	}

	public int getColor() {
		return dataManager.get(COLOR);
	}

	@Override
	public void dropItemOrSelf(Entity entityIn, boolean p_146065_2_) {
		if(!p_146065_2_) {
			super.dropItemOrSelf(entityIn, p_146065_2_);
			return;
		}

		if(getEntityWorld().getGameRules().getBoolean("doEntityDrops")) {
			ItemStack itemstack = getDisplayedItem();

			if(entityIn instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer)entityIn;

				if(entityplayer.capabilities.isCreativeMode) {
					removeFrameFromMap(itemstack);
					return;
				}
			}

			entityDropItem(new ItemStack(ColoredItemFrames.colored_item_frame, 1, getColor()), 0.0F);

			if(!itemstack.isEmpty() && rand.nextFloat() < itemDropChance) {
				itemstack = itemstack.copy();
				removeFrameFromMap(itemstack);
				entityDropItem(itemstack, 0.0F);
			}

		}
	}

	private void removeFrameFromMap(ItemStack stack) {
		if(!stack.isEmpty()) {
			if(stack.getItem() instanceof ItemMap) {
				MapData mapdata = ((ItemMap) stack.getItem()).getMapData(stack, getEntityWorld());
				mapdata.mapDecorations.remove("frame-" + getEntityId());
			}

			stack.setItemFrame((EntityItemFrame) null);
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		compound.setInteger(TAG_COLOR, getColor());
		super.writeEntityToNBT(compound);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		dataManager.set(COLOR, compound.getInteger(TAG_COLOR));

		if(compound.hasKey(TAG_ITEMDROPCHANCE, 99)) {
			itemDropChance = compound.getFloat(TAG_ITEMDROPCHANCE);
		}

		super.readEntityFromNBT(compound);
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeShort(facingDirection.getHorizontalIndex());
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		updateFacingWithBoundingBox(EnumFacing.getHorizontal(additionalData.readShort()));
	}

}
