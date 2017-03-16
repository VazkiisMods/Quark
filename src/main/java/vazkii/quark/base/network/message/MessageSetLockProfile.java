package vazkii.quark.base.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.quark.misc.feature.LockDirectionHotkey;
import vazkii.quark.misc.feature.LockDirectionHotkey.LockProfile;

public class MessageSetLockProfile extends NetworkMessage<MessageSetLockProfile> {

	public LockProfile profile; 
	
	public MessageSetLockProfile() { }
	
	public MessageSetLockProfile(LockProfile profile) {
		this.profile = profile;
	}
	
	@Override
	public IMessage handleMessage(MessageContext context) {
		LockDirectionHotkey.setProfile(context.getServerHandler().playerEntity, profile);
		return null;
	}
	
}
