/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * 
 * File Created @ [15/07/2016, 06:03:43 (GMT)]
 */
package vazkii.quark.base.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.quark.base.module.GlobalConfig;

public class MessageChangeConfig extends NetworkMessage {
	
	public String moduleName;
	public String category;
	public String key;
	public String value;
	public boolean saveToFile;
	
	public MessageChangeConfig() { }

	public MessageChangeConfig(String moduleName, String category, String key, String value, boolean saveToFile) {
		this.moduleName = moduleName;
		this.category = category;
		this.key = key;
		this.value = value;
	}
	
	@Override
	public IMessage handleMessage(MessageContext context) {
		GlobalConfig.changeConfig(moduleName, category, key, value, saveToFile);
		return null;
	}
	
}
