package vazkii.quark.base.network.message;

import vazkii.quark.content.tweaks.module.LockRotationModule;
import vazkii.quark.content.tweaks.module.LockRotationModule.LockProfile;
import vazkii.zeta.network.IZetaMessage;
import vazkii.zeta.network.IZetaNetworkEventContext;

import java.io.Serial;

public class SetLockProfileMessage implements IZetaMessage {

	@Serial
	private static final long serialVersionUID = 1037317801540162515L;

	public LockProfile profile;

	public SetLockProfileMessage() {}

	public SetLockProfileMessage(LockProfile profile) {
		this.profile = profile;
	}

	@Override
	public boolean receive(IZetaNetworkEventContext context) {
		context.enqueueWork(() -> LockRotationModule.setProfile(context.getSender(), profile));
		return true;
	}

}
