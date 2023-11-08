package org.violetmoon.quark.base.network.message;

import org.violetmoon.quark.content.tweaks.module.LockRotationModule;
import org.violetmoon.quark.content.tweaks.module.LockRotationModule.LockProfile;
import org.violetmoon.zeta.network.IZetaMessage;
import org.violetmoon.zeta.network.IZetaNetworkEventContext;

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
