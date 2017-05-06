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

import com.google.common.base.Predicate;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nullable;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import org.apache.commons.lang3.Validate;
import vazkii.quark.decoration.feature.ColoredItemFrames;

public class EntityColoredItemFrame extends EntityFlatItemFrame {

	private static final DataParameter<Integer> COLOR = EntityDataManager.<Integer>createKey(EntityColoredItemFrame.class, DataSerializers.VARINT);
	private static final String TAG_COLOR = "DyeColor";

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
	protected void dropFrame() {
		entityDropItem(new ItemStack(ColoredItemFrames.colored_item_frame, 1, getColor()), 0.0F);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		compound.setInteger(TAG_COLOR, getColor());
		super.writeEntityToNBT(compound);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		dataManager.set(COLOR, compound.getInteger(TAG_COLOR));
		super.readEntityFromNBT(compound);
	}
}
