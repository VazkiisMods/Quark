/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 * File Created @ [Jan 21, 2014, 9:18:28 PM (GMT)]
 */
package vazkii.arl.block.be;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.arl.util.VanillaPacketDispatcher;

public abstract class ARLBlockEntity extends BlockEntity {

	public ARLBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);
	}

	@Override
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		
		writeSharedNBT(nbt);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);

		readSharedNBT(nbt);
	}

	public void writeSharedNBT(CompoundTag cmp) {
		// NO-OP
	}

	public void readSharedNBT(CompoundTag cmp) {
		// NO-OP
	}
	
	public void sync() {
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag cmp = new CompoundTag();
		writeSharedNBT(cmp);
		return cmp;
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
		super.onDataPacket(net, packet);
		
		if(packet != null)
			readSharedNBT(packet.getTag());
	}

}
