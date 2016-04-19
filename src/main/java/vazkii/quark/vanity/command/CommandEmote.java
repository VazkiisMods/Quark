/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [26/03/2016, 22:09:14 (GMT)]
 */
package vazkii.quark.vanity.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import vazkii.quark.base.network.NetworkHandler;
import vazkii.quark.base.network.message.MessageDoEmote;

public class CommandEmote extends CommandBase {

	@Override
	public String getCommandName() {
		return "emote";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "<emote>";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return sender instanceof EntityPlayer;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length > 0 && sender instanceof EntityPlayer)
			NetworkHandler.INSTANCE.sendToAll(new MessageDoEmote(args[0], sender.getName()));
	}
}
