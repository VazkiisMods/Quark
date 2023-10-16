/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 * File Created @ [Apr 9, 2015, 9:38:44 PM (GMT)]
 */
package vazkii.arl.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

public final class VanillaPacketDispatcher {

	public static void dispatchTEToNearbyPlayers(BlockEntity tile) {
		Level world = tile.getLevel();
		if(world instanceof ServerLevel) {
			Packet<ClientGamePacketListener> packet = tile.getUpdatePacket();
			BlockPos pos = tile.getBlockPos();
			
			for(Player player : world.players()) {
				if(player instanceof ServerPlayer && pointDistancePlane(player.getX(), player.getZ(), pos.getX(), pos.getZ()) < 64)
					((ServerPlayer) player).connection.send(packet);
			}
		}
	}

	public static void dispatchTEToNearbyPlayers(Level world, BlockPos pos) {
		BlockEntity tile = world.getBlockEntity(pos);
		if(tile != null)
			dispatchTEToNearbyPlayers(tile);
	}

	public static float pointDistancePlane(double x1, double y1, double x2, double y2) {
		return (float) Math.hypot(x1 - x2, y1 - y2);
	}

}