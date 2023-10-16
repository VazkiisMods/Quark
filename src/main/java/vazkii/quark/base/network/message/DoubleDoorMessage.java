package vazkii.quark.base.network.message;

import java.io.Serial;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import vazkii.arl.network.IMessage;
import vazkii.quark.content.tweaks.module.DoubleDoorOpeningModule;
import vazkii.zeta.network.IZetaMessage;
import vazkii.zeta.network.IZetaNetworkEventContext;

public class DoubleDoorMessage implements IZetaMessage {

	@Serial
	private static final long serialVersionUID = 8024722624953236124L;

	public BlockPos pos;

	public DoubleDoorMessage() { }

	public DoubleDoorMessage(BlockPos pos) {
		this.pos = pos;
	}

	private Level extractWorld(ServerPlayer entity) {
		return entity == null ? null : entity.level;
	}

	@Override
	public boolean receive(IZetaNetworkEventContext context) {
		context.enqueueWork(() -> DoubleDoorOpeningModule.openBlock(extractWorld(context.getSender()), context.getSender(), pos));
		return true;
	}

}
