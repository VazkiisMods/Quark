package vazkii.quark.experimental.features;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.lib.LibObfuscation;
import vazkii.quark.base.module.Feature;

public class ReactiveCursor extends Feature {

	private static final String CURSOR_ROOT = "textures/cursors/"; 
	private static final HashMap<String, Cursor> CURSORS = new HashMap();

	private static final String CURSOR_POINTER = "pointer";
	private static final String CURSOR_FINGER = "finger";
	private static final String CURSOR_OPEN_HAND = "open_hand";
	private static final String CURSOR_CLOSED_HAND = "closed_hand";
	private static final String CURSOR_SAW = "saw";

	String currentCursor = "";

	@Override
	@SideOnly(Side.CLIENT)
	public void initClient(FMLInitializationEvent event) {
		putCursor(CURSOR_POINTER, 0, 1);
		putCursor(CURSOR_FINGER, 0.4, 1);
		putCursor(CURSOR_OPEN_HAND, 0.4, 1);
		putCursor(CURSOR_CLOSED_HAND, 0.4, 1);
		putCursor(CURSOR_SAW, 1, 1);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onTick(RenderTickEvent event) {
		if(event.phase == Phase.END) {
			String cursorName = getCursor();

			if(!currentCursor.equals(cursorName)) {
				Cursor cursor = CURSORS.get(cursorName);
				try {
					if(cursor != null)
						Mouse.setNativeCursor(cursor);
				} catch (LWJGLException e) {
					e.printStackTrace();
				}
				currentCursor = cursorName;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private String getCursor() {
		Minecraft mc = Minecraft.getMinecraft();
		GuiScreen gui = mc.currentScreen;
		if(gui != null) {
			if(gui instanceof GuiContainer) {
				GuiContainer container = (GuiContainer) gui;
				if(!mc.player.inventory.getItemStack().isEmpty())
					return CURSOR_CLOSED_HAND;

				Slot hovered = container.getSlotUnderMouse();
				if(hovered != null && !hovered.getStack().isEmpty()) {
					if(hovered instanceof SlotCrafting)
						return CURSOR_SAW;
					return CURSOR_OPEN_HAND;
				}
			}

			List<GuiButton> buttonList = ReflectionHelper.getPrivateValue(GuiScreen.class, gui, LibObfuscation.BUTTON_LIST);
			for(GuiButton b : buttonList)
				if(b.isMouseOver())
					return CURSOR_FINGER;
		}

		return CURSOR_POINTER;
	}

	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}

	@SideOnly(Side.CLIENT)
	private static void putCursor(String name, double xAnchor, double yAnchor) {
		putCursor(name, name + ".png", xAnchor, yAnchor);
	}

	@SideOnly(Side.CLIENT)
	private static void putCursor(String name, String file, double xAnchor, double yAnchor) {
		putCursor(name, new ResourceLocation(LibMisc.MOD_ID, CURSOR_ROOT + file), xAnchor, yAnchor);
	}

	@SideOnly(Side.CLIENT)
	private static void putCursor(String name, ResourceLocation res, double xAnchor, double yAnchor) {
		try {
			CURSORS.put(name, null);

			Minecraft mc = Minecraft.getMinecraft();
			InputStream stream = mc.getResourceManager().getResource(res).getInputStream();
			BufferedImage img = ImageIO.read(stream);

			int width = img.getWidth();
			int height = img.getHeight();

			if(width == 0 || height == 0)
				throw new IOException("Invalid image " + res + " (Img: " + img + ", Stream: " + stream + ")");

			int[] colors = new int[width * height];
			ByteBuffer buf = BufferUtils.createByteBuffer(colors.length * 4);

			img.getRGB(0, 0, width, height, colors, 0, width);
			int pixels = 0;

			for(int i = colors.length - 1; i >= 0; i--) {
				int color = colors[(i / height * height) + (width - (i % width) - 1)];
				int a = (color >> 24) & 0xFF;
				int r = (color >> 16) & 0xFF;
				int g = (color >> 8) & 0xFF;
				int b = color & 0xFF;

				buf.put((byte) b);
				buf.put((byte) g);
				buf.put((byte) r);
				buf.put((byte) a);
			}

			buf.flip();

			int xHotspot = MathHelper.clamp((int) ((double) width * xAnchor), 0, width - 1);
			int yHotspot = MathHelper.clamp((int) ((double) height * yAnchor), 0, height - 1);

			Cursor cursor = new Cursor(width, height, xHotspot, yHotspot, 1, buf.asIntBuffer(), null);
			CURSORS.put(name, cursor);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
