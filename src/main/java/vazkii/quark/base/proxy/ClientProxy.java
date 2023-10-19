package vazkii.quark.base.proxy;

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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import vazkii.quark.base.Quark;
import vazkii.quark.base.QuarkClient;
import vazkii.quark.base.client.config.IngameConfigHandler;
import vazkii.quark.base.client.config.QButtonHandler;
import vazkii.quark.base.client.config.external.ExternalConfigHandler;
import vazkii.quark.base.client.config.screen.QuarkConfigHomeScreen;
import vazkii.quark.base.client.handler.InventoryButtonHandler;
import vazkii.quark.base.client.handler.ModelHandler;
import vazkii.quark.base.client.handler.NetworkProfilingHandler;
import vazkii.quark.base.client.handler.RequiredModTooltipHandler;
import vazkii.quark.base.client.handler.TopLayerTooltipHandler;
import vazkii.quark.base.handler.ContributorRewardHandler;
import vazkii.quark.base.handler.DyeHandler;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.WoodSetHandler;
import vazkii.quark.base.module.config.IConfigCallback;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.structural.C2SUpdateFlag;
import vazkii.quark.mixin.client.accessor.AccessorMultiPlayerGameMode;
import vazkii.zeta.event.client.ZClientModulesReady;
import vazkii.zeta.event.client.ZConfigChangedClient;

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
			.subscribe(ContributorRewardHandler.Client.class)
			.subscribe(DyeHandler.Client.class)
			.subscribe(RenderLayerHandler.Client.class)
			.subscribe(WoodSetHandler.Client.class);

		//Formerly @EventBusSubscribers - gathered here to make them more visible
		FMLJavaModLoadingContext.get().getModEventBus().register(ModelHandler.class);
		MinecraftForge.EVENT_BUS.register(ContributorRewardHandler.Client.class);
		MinecraftForge.EVENT_BUS.register(InventoryButtonHandler.class);
		MinecraftForge.EVENT_BUS.register(MiscUtil.Client.class);
		MinecraftForge.EVENT_BUS.register(NetworkProfilingHandler.class);
		MinecraftForge.EVENT_BUS.register(QButtonHandler.class);
		MinecraftForge.EVENT_BUS.register(RequiredModTooltipHandler.class);
		MinecraftForge.EVENT_BUS.register(TopLayerTooltipHandler.class);

		super.start(); //<- loads and initializes modules
		Quark.ZETA.loadBus.fire(new ZClientModulesReady());

		ModLoadingContext.get().registerExtensionPoint(ConfigScreenFactory.class, () -> new ConfigScreenFactory((minecraft, screen) -> new QuarkConfigHomeScreen(screen)));

		copyProgrammerArtIfMissing();

		(new ExternalConfigHandler()).setAPIHandler();
	}

	@Override
	public void handleQuarkConfigChange() {
		super.handleQuarkConfigChange();

		Quark.ZETA.loadBus.fire(new ZConfigChangedClient());

		if (Minecraft.getInstance().getConnection() != null)
			QuarkNetwork.sendToServer(C2SUpdateFlag.createPacket());
		IngameConfigHandler.INSTANCE.refresh();

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
	public IConfigCallback getConfigCallback() {
		return IngameConfigHandler.INSTANCE;
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

