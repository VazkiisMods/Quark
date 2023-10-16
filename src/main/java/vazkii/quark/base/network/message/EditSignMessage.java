package vazkii.quark.base.network.message;

import net.minecraft.core.BlockPos;

import vazkii.quark.content.tweaks.module.SignEditingModule;
import vazkii.zeta.network.IZetaMessage;
import vazkii.zeta.network.IZetaNetworkEventContext;

import java.io.Serial;

public class EditSignMessage implements IZetaMessage {

	@Serial
	private static final long serialVersionUID = -329145938273036832L;

	public BlockPos pos;

	public EditSignMessage() {}

	public EditSignMessage(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public boolean receive(IZetaNetworkEventContext context) {
		context.enqueueWork(() -> SignEditingModule.openSignGuiClient(pos));

		return true;
	}

}
