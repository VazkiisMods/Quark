package org.violetmoon.quark.base.proxy;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.fml.ModLoadingContext;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.QuarkClient;
import org.violetmoon.quark.base.client.config.QButtonHandler;
import org.violetmoon.quark.base.client.config.QuarkConfigHomeScreen;
import org.violetmoon.quark.base.client.handler.InventoryButtonHandler;
import org.violetmoon.quark.base.client.handler.ModelHandler;
import org.violetmoon.quark.base.client.handler.NetworkProfilingHandler;
import org.violetmoon.quark.base.client.handler.RequiredModTooltipHandler;
import org.violetmoon.quark.base.handler.ContributorRewardHandler;
import org.violetmoon.quark.base.handler.MiscUtil;
import org.violetmoon.quark.base.handler.WoodSetHandler;
import org.violetmoon.quark.base.network.QuarkNetwork;
import org.violetmoon.quark.base.network.message.structural.C2SUpdateFlag;
import org.violetmoon.quark.mixin.client.accessor.AccessorMultiPlayerGameMode;
import org.violetmoon.zeta.client.TopLayerTooltipHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.Month;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {

	public static boolean jingleBellsMotherfucker = false;

	@Override
	public void start() {
		LocalDateTime now = LocalDateTime.now();
		if(now.getMonth() == Month.DECEMBER && now.getDayOfMonth() >= 16 || now.getMonth() == Month.JANUARY && now.getDayOfMonth() <= 6)
			jingleBellsMotherfucker = true;

		//initialize ZetaClient
		QuarkClient.start();

		Quark.ZETA.loadBus
			.subscribe(ModelHandler.class) //TODO: Make this especially not a singleton, move it into respective modules
			.subscribe(ContributorRewardHandler.Client.class)
			.subscribe(WoodSetHandler.Client.class);

		Quark.ZETA.playBus
			.subscribe(NetworkProfilingHandler.class)
			.subscribe(ContributorRewardHandler.Client.class)
			.subscribe(MiscUtil.Client.class)
			.subscribe(InventoryButtonHandler.class)
			.subscribe(QButtonHandler.class)
			.subscribe(TopLayerTooltipHandler.class)
			.subscribe(new RequiredModTooltipHandler.Client(Quark.ZETA)); //TODO: I think this can be spread into ZetaItem/ZetaBlock and not a singleton

		super.start(); //<- loads and initializes modules

		ModLoadingContext.get().registerExtensionPoint(ConfigScreenFactory.class, () -> new ConfigScreenFactory((minecraft, screen) -> new QuarkConfigHomeScreen(screen)));

		copyProgrammerArtIfMissing();
	}

	@Override
	public void handleQuarkConfigChange() {
		super.handleQuarkConfigChange();

		if (Minecraft.getInstance().getConnection() != null)
			QuarkNetwork.sendToServer(C2SUpdateFlag.createPacket());

		Minecraft mc = Minecraft.getInstance();
		mc.submit(() -> {
			if(mc.hasSingleplayerServer() && mc.player != null && mc.getSingleplayerServer() != null)
				for(int i = 0; i < 3; i++)
					mc.player.sendSystemMessage(Component.translatable("quark.misc.reloaded" + i).withStyle(i == 0 ? ChatFormatting.AQUA : ChatFormatting.WHITE));
		});
	}

	@Override
	public InteractionResult clientUseItem(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		if (player instanceof LocalPlayer lPlayer) {
			var mc = Minecraft.getInstance();
			if (mc.gameMode != null && mc.level != null) {
				if (!mc.level.getWorldBorder().isWithinBounds(hit.getBlockPos())) {
					return InteractionResult.FAIL;
				} else {
					return ((AccessorMultiPlayerGameMode) mc.gameMode).quark$performUseItemOn(lPlayer, hand, hit);
				}
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public boolean isClientPlayerHoldingShift() {
		return Screen.hasShiftDown();
	}

	private static void copyProgrammerArtIfMissing() {
		File dir = new File(".", "resourcepacks");
		File target = new File(dir, "Quark Programmer Art.zip");

		if(!target.exists())
			try {
				dir.mkdirs();
				InputStream in = Quark.class.getResourceAsStream("/assets/quark/programmer_art.zip");
				FileOutputStream out = new FileOutputStream(target);

				byte[] buf = new byte[16384];
				int len;
				while((len = in.read(buf)) > 0)
					out.write(buf, 0, len);

				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

}

