package vazkii.quark.tweaks.feature;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.HoverEvent.Action;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.MouseInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.actors.threadpool.Arrays;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.message.MessageUpdateAfk;

public class ImprovedSleeping extends Feature {

	private int timeSinceKeystroke;
	private static List<String> sleepingPlayers = new ArrayList();

	private static boolean enableAfk;
	private static int afkTime, percentReq;
	
	private static String TAG_AFK = "quark:afk";

	@Override
	public void setupConfig() {
		enableAfk = loadPropBool("Enable AFK", "", true);
		afkTime = loadPropInt("Time for AFK", "How many ticks are required for a player to be marked AFK", 2 * 1200);
		percentReq = loadPropInt("Required Percentage", "The percentage of the (non-afk) server that needs to be sleeping for the time to change.", 100);
	}

	public static void updateAfk(EntityPlayer player, boolean afk) {
		if(!enableAfk)
			return;

		if(player.world.playerEntities.size() != 1) {
			if(afk) {
				player.getEntityData().setBoolean(TAG_AFK, true);
				TextComponentTranslation text = new TextComponentTranslation("quarkmisc.nowAfk");
				text.getStyle().setColor(TextFormatting.AQUA);
				player.sendMessage(text);
			} else {
				player.getEntityData().setBoolean(TAG_AFK, false);
				TextComponentTranslation text = new TextComponentTranslation("quarkmisc.leftAfk");
				text.getStyle().setColor(TextFormatting.AQUA);
				player.sendMessage(text);
			}
		}
	}

	public static int isEveryoneAsleep(World world) {
		if(!ModuleLoader.isFeatureEnabled(ImprovedSleeping.class))
			return 0;

		Pair<Integer, Integer> counts = getPlayerCounts(world);
		int legitPlayers = counts.getLeft();
		int sleepingPlayers = counts.getRight();

		boolean everybody = (legitPlayers > 0 && ((float) sleepingPlayers / (float) legitPlayers) * 100 >= percentReq);
		return everybody ? 2 : 1;
	}

	private static boolean doesPlayerCountForSleeping(EntityPlayer player) {
		return !player.isSpectator() && !player.getEntityData().getBoolean(TAG_AFK);
	}

	private static boolean isPlayerSleeping(EntityPlayer player) {
		return player.isPlayerFullyAsleep();
	}

	private static Pair<Integer, Integer> getPlayerCounts(World world) {
		int legitPlayers = 0;
		int sleepingPlayers = 0;
		for(EntityPlayer player : world.playerEntities)
			if(doesPlayerCountForSleeping(player)) {
				legitPlayers++;

				if(isPlayerSleeping(player))
					sleepingPlayers++;
			}

		return Pair.of(legitPlayers, sleepingPlayers);
	}

	@SubscribeEvent
	public void onWorldTick(PlayerTickEvent event) {
		World world = event.player.world;
		if(world.isRemote || world.provider.getDimension() != 0 || world.playerEntities.indexOf(event.player) != 0 || event.phase != Phase.END)
			return;
		
		List<String> newSleepingPlayers = new ArrayList();
		List<String> nonSleepingPlayers = new ArrayList();
		int legitPlayers = 0;
		String sleeper = "";

		for(EntityPlayer player : world.playerEntities)
			if(doesPlayerCountForSleeping(player)) {
				String name = player.getName();
				if(isPlayerSleeping(player)) {
					if(!sleepingPlayers.contains(name))
						sleeper = name;
					newSleepingPlayers.add(name);
				} else nonSleepingPlayers.add(name);
				
				legitPlayers++;
			}
		sleepingPlayers = newSleepingPlayers;
		
		if(!sleeper.isEmpty() && world.playerEntities.size() != 1) {
			int requiredPlayers = Math.max((int) Math.ceil(((float) legitPlayers * (float) percentReq / 100F)), 0);
			
			TextComponentBase message = new TextComponentTranslation("quarkmisc.personSleeping", sleeper);
			message.getStyle().setColor(TextFormatting.GOLD);
			message.appendSibling(new TextComponentString(" "));
			
			List<String> lines = new ArrayList();
			for(String s : newSleepingPlayers)
				lines.add("\u00A7a\u2714 " + s);
			for(String s : nonSleepingPlayers)
				lines.add("\u00A7c\u2718 " + s);
			
			TextComponentBase hoverText = new TextComponentTranslation("quarkmisc.sleepingListHeader", "\n");
			for(int i = 0; i < lines.size(); i++)
				hoverText.appendSibling(new TextComponentString(lines.get(i) + (i == lines.size() - 1 ? "" : "\n")));
			
			TextComponentBase sibling = new TextComponentString(String.format("(%d/%d)", newSleepingPlayers.size(), requiredPlayers));
			HoverEvent hover = new HoverEvent(Action.SHOW_TEXT, hoverText);
			sibling.getStyle().setHoverEvent(hover);
			sibling.getStyle().setUnderlined(true);
			message.appendSibling(sibling);
			
			for(EntityPlayer player : world.playerEntities)
				player.sendMessage(message);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onClientTick(ClientTickEvent event) {
		if(event.phase == Phase.END) {
			timeSinceKeystroke++;

			if(timeSinceKeystroke == afkTime)
				NetworkHandler.INSTANCE.sendToServer(new MessageUpdateAfk(true));
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onKeystroke(KeyInputEvent event) {
		registerPress();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onKeystroke(GuiScreenEvent.KeyboardInputEvent.Pre event) {
		registerPress();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onMousePress(MouseInputEvent event) {
		if(Mouse.getEventButtonState() && Minecraft.getMinecraft().currentScreen != null)
			registerPress();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onMousePress(GuiScreenEvent.MouseInputEvent.Pre event) {
		if(Mouse.getEventButtonState() && Minecraft.getMinecraft().currentScreen != null)
			registerPress();
	}

	private void registerPress() {
		if(timeSinceKeystroke >= afkTime)
			NetworkHandler.INSTANCE.sendToServer(new MessageUpdateAfk(false));
		timeSinceKeystroke = 0;
	}

    @SubscribeEvent
    @SideOnly(Side.SERVER)
    public void onPlayerLogout(PlayerLoggedOutEvent event) {
        World logoutWorld = event.player.world;
        List<EntityPlayer> players = logoutWorld.playerEntities;
        if(players.size() == 1) {
            EntityPlayer lastPlayer = players.get(0);
            lastPlayer.getEntityData().setBoolean(TAG_AFK, false);
            TextComponentTranslation text = new TextComponentTranslation("quarkmisc.leftAfk");
            text.getStyle().setColor(TextFormatting.AQUA);
            lastPlayer.sendMessage(text);
        }
    }

	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
	@Override
	public String[] getIncompatibleMods() {
		return new String[] { "morpheus" };
	}

}
