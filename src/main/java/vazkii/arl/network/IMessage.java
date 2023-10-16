package vazkii.arl.network;

import java.io.Serializable;

import net.minecraftforge.network.NetworkEvent;

public interface IMessage extends Serializable {

	public boolean receive(NetworkEvent.Context context);
	
}