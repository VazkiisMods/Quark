package org.violetmoon.quark.base.network.message.oddities;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

import org.violetmoon.quark.addons.oddities.block.be.MatrixEnchantingTableBlockEntity;
import org.violetmoon.quark.addons.oddities.inventory.MatrixEnchantingMenu;
import org.violetmoon.zeta.network.IZetaMessage;
import org.violetmoon.zeta.network.IZetaNetworkEventContext;

import java.io.Serial;

public class MatrixEnchanterOperationMessage implements IZetaMessage {

	@Serial
	private static final long serialVersionUID = 2272401655489445173L;

	public int operation;
	public int arg0, arg1, arg2;

	public MatrixEnchanterOperationMessage() {}

	public MatrixEnchanterOperationMessage(int operation, int arg0, int arg1, int arg2) {
		this.operation = operation;
		this.arg0 = arg0;
		this.arg1 = arg1;
		this.arg2 = arg2;
	}

	@Override
	public boolean receive(IZetaNetworkEventContext context) {
		context.enqueueWork(() -> {
			ServerPlayer player = context.getSender();
			AbstractContainerMenu container = player.containerMenu;

			if(container instanceof MatrixEnchantingMenu matrixMenu) {
				MatrixEnchantingTableBlockEntity enchanter = matrixMenu.enchanter;
				enchanter.onOperation(player, operation, arg0, arg1, arg2);
			}
		});

		return true;
	}

}
