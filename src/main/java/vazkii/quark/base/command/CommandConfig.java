/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * 
 * File Created @ [15/07/2016, 05:22:28 (GMT)]
 */
package vazkii.quark.base.command;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.base.network.message.MessageChangeConfig;

public class CommandConfig extends CommandBase {

	private static final Pattern TOKENIZER = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
	
	@Override
	public String getName() {
		return "quarkconfig";
	}

	@Override
	public String getUsage(ICommandSender p_71518_1_) {
		return "<module> <category> <key> <value> [save?] [player]";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;	
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		String fullInput = String.join(" ", args);
		Matcher m = TOKENIZER.matcher(fullInput);
		
		List<String> matches = new ArrayList();
		while(m.find()) {
			String s = m.group(0);
			if(s.startsWith("\"") && s.endsWith("\"")) {
				s = s.replaceAll("\"$", "");
				s = s.replaceAll("^\"", "");
			}
				
			matches.add(s);
		}
		
		if(matches.size() < 4)
			 throw new SyntaxErrorException();
				
		String fileStr = matches.get(0);
		if(fileStr.contains("\\.\\."))
			throw new WrongUsageException("I know the game you're playing.");
		
		boolean save = matches.size() > 4 && matches.get(4).equals("save");
		
		String moduleName = matches.get(0);
		String category = matches.get(1);
		String key = matches.get(2);
		String value = matches.get(3);
		
		GlobalConfig.changeConfig(moduleName, category, key, value, save);
		
		String player = matches.size() > 5 ? player = matches.get(5) : null;
		if(player != null) {
			EntityPlayerMP playermp = getPlayer(server, sender, player);
			NetworkHandler.INSTANCE.sendTo(new MessageChangeConfig(moduleName, category, key, value, save), playermp);
		} else NetworkHandler.INSTANCE.sendToAll(new MessageChangeConfig(moduleName, category, key, value, save));
	}

}
