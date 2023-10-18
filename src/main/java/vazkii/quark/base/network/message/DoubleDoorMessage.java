package vazkii.quark.base.network.message;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import vazkii.quark.base.Quark;
import vazkii.quark.content.tweaks.module.DoubleDoorOpeningModule;
import vazkii.zeta.network.IZetaMessage;
import vazkii.zeta.network.IZetaNetworkEventContext;

import java.io.Serial;

public class DoubleDoorMessage implements IZetaMessage {

	@Serial
	private static final long serialVersionUID = 8024722624953236124L;

	public BlockPos pos;

	public DoubleDoorMessage() {}

	public DoubleDoorMessage(BlockPos pos) {
		this.pos = pos;
	}

	private Level extractWorld(ServerPlayer entity) {
		return entity == null ? null : entity.level;
	}

	@Override
	public boolean receive(IZetaNetworkEventContext context) {
		context.enqueueWork(() -> Quark.ZETA.modules.get(DoubleDoorOpeningModule.class).openBlock(extractWorld(context.getSender()), context.getSender(), pos));
		return true;
	}

}
