package vazkii.quark.addons.oddities.client.screen;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import vazkii.quark.addons.oddities.block.be.MatrixEnchantingTableBlockEntity;
import vazkii.quark.addons.oddities.inventory.EnchantmentMatrix;
import vazkii.quark.addons.oddities.inventory.EnchantmentMatrix.Piece;
import vazkii.quark.addons.oddities.inventory.MatrixEnchantingMenu;
import vazkii.quark.addons.oddities.module.MatrixEnchantingModule;
import vazkii.quark.base.Quark;
import vazkii.quark.base.QuarkClient;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.oddities.MatrixEnchanterOperationMessage;

public class MatrixEnchantingScreen extends AbstractContainerScreen<MatrixEnchantingMenu> {

	public static final ResourceLocation BACKGROUND = new ResourceLocation(Quark.MOD_ID, "textures/misc/matrix_enchanting.png");

	protected final Inventory playerInv;
	protected final MatrixEnchantingTableBlockEntity enchanter;

	protected Button plusButton;
	protected MatrixEnchantingPieceList pieceList;
	protected Piece hoveredPiece;

	protected int selectedPiece = -1;
	protected int gridHoverX, gridHoverY;
	protected List<Integer> listPieces = null;

	public MatrixEnchantingScreen(MatrixEnchantingMenu container, Inventory inventory, Component component) {
		super(container, inventory, component);
		this.playerInv = inventory;
		this.enchanter = container.enchanter;
	}

	@Override
	public void init() {
		super.init();

		selectedPiece = -1;
		addRenderableWidget(plusButton = new MatrixEnchantingPlusButton(leftPos + 86, topPos + 63, this::add));
		pieceList = new MatrixEnchantingPieceList(this, 28, 64, topPos + 11, topPos + 75, 22);
		pieceList.setLeftPos(leftPos + 139);
		addRenderableWidget(pieceList);
		updateButtonStatus();

		pieceList.refresh();
	}

	@Override
	public void containerTick() {
		super.containerTick();
		updateButtonStatus();

		if(enchanter.matrix == null) {
			selectedPiece = -1;
			pieceList.refresh();
		}

		if(enchanter.clientMatrixDirty) {
			pieceList.refresh();
			enchanter.clientMatrixDirty = false;
		}
	}

	@Override
	protected void renderBg(@Nonnull PoseStack stack, float partialTicks, int mouseX, int mouseY) {
		Minecraft mc = getMinecraft();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, BACKGROUND);

		int i = leftPos;
		int j = topPos;
		blit(stack, i, j, 0, 0, imageWidth, imageHeight);

		if(enchanter.charge > 0 && MatrixEnchantingModule.chargePerLapis > 0) {
			int maxHeight = 18;
			int barHeight = (int) (((float) enchanter.charge / MatrixEnchantingModule.chargePerLapis) * maxHeight);
			blit(stack, i + 7, j + 32 + maxHeight - barHeight, 50, 176 + maxHeight - barHeight, 4, barHeight);
		}

		pieceList.render(stack, mouseX, mouseY, partialTicks);

		if(enchanter.matrix != null
			 && enchanter.matrix.canGeneratePiece(enchanter.influences, enchanter.bookshelfPower, enchanter.enchantability)
			 && !mc.player.getAbilities().instabuild) {
			int x = i + 74;
			int y = j + 58;
			int xpCost = enchanter.matrix.getNewPiecePrice();
			int xpMin = enchanter.matrix.getMinXpLevel(enchanter.bookshelfPower);
			boolean has = enchanter.matrix.validateXp(mc.player, enchanter.bookshelfPower);
			blit(stack, x, y, 0, imageHeight, 10, 10);
			String text = String.valueOf(xpCost);

			if(!has && mc.player.experienceLevel < xpMin) {
				font.drawShadow(stack, "!", x + 6, y + 3, 0xFF0000);
				text = I18n.get("quark.gui.enchanting.min", xpMin);
			}

			x -= (font.width(text) - 5);
			y += 3;
			font.draw(stack, text, x - 1, y, 0);
			font.draw(stack, text, x + 1, y, 0);
			font.draw(stack, text, x, y + 1, 0);
			font.draw(stack, text, x, y - 1, 0);
			font.draw(stack, text, x, y, has ? 0xc8ff8f : 0xff8f8f);
		}
	}

	@Override
	protected void renderLabels(@Nonnull PoseStack matrix, int mouseX, int mouseY) {
		int color = MiscUtil.Client.getGuiTextColor("matrix_enchanting");

		font.draw(matrix, enchanter.getDisplayName().getString(), 12, 5, color);
		font.draw(matrix, playerInv.getDisplayName().getString(), 8, imageHeight - 96 + 2, color);

		if(enchanter.matrix != null) {
			boolean needsRefresh = listPieces == null;
			listPieces = enchanter.matrix.benchedPieces;
			if(needsRefresh)
				pieceList.refresh();
			renderMatrixGrid(matrix, enchanter.matrix);
		}
	}
	@Override
	public void render(@Nonnull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(stack);
		super.render(stack, mouseX, mouseY, partialTicks);

		if(hoveredPiece != null) {
			List<Component> tooltip = new LinkedList<>();
			tooltip.add(Component.translatable(hoveredPiece.enchant.getFullname(hoveredPiece.level).getString().replaceAll("\\u00A7.", "")).withStyle(ChatFormatting.GOLD));

			if(hoveredPiece.influence > 0)
				tooltip.add(Component.translatable("quark.gui.enchanting.influence", (int) (hoveredPiece.influence * MatrixEnchantingModule.influencePower * 100)).withStyle(ChatFormatting.GRAY));
			else if (hoveredPiece.influence < 0)
				tooltip.add(Component.translatable("quark.gui.enchanting.dampen", (int) (hoveredPiece.influence * MatrixEnchantingModule.influencePower * 100)).withStyle(ChatFormatting.GRAY));

			int max = hoveredPiece.getMaxXP();
			if(max > 0)
				tooltip.add(Component.translatable("quark.gui.enchanting.upgrade", hoveredPiece.xp, max).withStyle(ChatFormatting.GRAY));

			if(gridHoverX == -1) {
				tooltip.add(Component.literal(""));
				tooltip.add(Component.translatable("quark.gui.enchanting.left_click").withStyle(ChatFormatting.GRAY));
				tooltip.add(Component.translatable("quark.gui.enchanting.right_click").withStyle(ChatFormatting.GRAY));
			} else if(selectedPiece != -1) {
				Piece p = getPiece(selectedPiece);
				if(p != null && p.enchant == hoveredPiece.enchant && hoveredPiece.level < hoveredPiece.enchant.getMaxLevel()) {
					tooltip.add(Component.literal(""));
					tooltip.add(Component.translatable("quark.gui.enchanting.merge").withStyle(ChatFormatting.GRAY));
				}
			}

			renderComponentTooltip(stack, tooltip, mouseX, mouseY); // renderTooltip
		} else
			renderTooltip(stack, mouseX, mouseY);
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		int gridMouseX = (int) (mouseX - leftPos - 86);
		int gridMouseY = (int) (mouseY - topPos - 11);

		gridHoverX = gridMouseX < 0 ? -1 : gridMouseX / 10;
		gridHoverY = gridMouseY < 0 ? -1 : gridMouseY / 10;
		if(gridHoverX < 0 || gridHoverX > 4 || gridHoverY < 0 || gridHoverY > 4) {
			gridHoverX = -1;
			gridHoverY = -1;
			hoveredPiece = null;
		} else if(enchanter.matrix != null) {
			int hover = enchanter.matrix.matrix[gridHoverX][gridHoverY];
			hoveredPiece = getPiece(hover);
		}
		
		pieceList.mouseMoved(gridMouseX, gridMouseY);

		super.mouseMoved(mouseX, mouseY);
	}
	
	@Override
	public boolean mouseDragged(double p_97752_, double p_97753_, int p_97754_, double p_97755_, double p_97756_) {
		pieceList.mouseDragged(p_97752_, p_97753_, p_97754_, p_97755_, p_97756_);
		
		return super.mouseDragged(p_97752_, p_97753_, p_97754_, p_97755_, p_97756_);
	}
	
	@Override
	public boolean mouseReleased(double p_97812_, double p_97813_, int p_97814_) {
		pieceList.mouseReleased(p_97812_, p_97813_, p_97814_);
		
		return super.mouseReleased(p_97812_, p_97813_, p_97814_);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		pieceList.mouseClicked(mouseX, mouseY, mouseButton);
		
		if(enchanter.matrix == null)
			return true;

		if(mouseButton == 0 && gridHoverX != -1) { // left click
			int hover = enchanter.matrix.matrix[gridHoverX][gridHoverY];

			if(selectedPiece != -1) {
				if(hover == -1)
					place(selectedPiece, gridHoverX, gridHoverY);
				else merge(selectedPiece);
			} else {
				remove(hover);
				if(!hasShiftDown())
					selectedPiece = hover;
			}
		} else if(mouseButton == 1 && selectedPiece != -1) {
			rotate(selectedPiece);
		}

		return true;
	}

	private void renderMatrixGrid(PoseStack stack, EnchantmentMatrix matrix) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, BACKGROUND);

		stack.pushPose();
		stack.translate(86, 11, 0);

		for(int i : matrix.placedPieces) {
			Piece piece = getPiece(i);
			if (piece != null) {
				stack.pushPose();
				stack.translate(piece.x * 10, piece.y * 10, 0);
				renderPiece(stack, piece, 1F);
				stack.popPose();
			}
		}

		if(selectedPiece != -1 && gridHoverX != -1) {
			Piece piece = getPiece(selectedPiece);
			if(piece != null && !(hoveredPiece != null && piece.enchant == hoveredPiece.enchant && hoveredPiece.level < hoveredPiece.enchant.getMaxLevel())) {
				stack.pushPose();
				stack.translate(gridHoverX * 10, gridHoverY * 10, 0);

				float a = 0.2F;
				if(matrix.canPlace(piece, gridHoverX, gridHoverY))
					a = (float) ((Math.sin(QuarkClient.ticker.total * 0.2) + 1) * 0.4 + 0.4);

				renderPiece(stack, piece, a);
				stack.popPose();
			}
		}

		if(hoveredPiece == null && gridHoverX != -1)
			renderHover(stack, gridHoverX, gridHoverY);

		stack.popPose();
	}

	protected void renderPiece(PoseStack stack, Piece piece, float a) {
		float r = ((piece.color >> 16) & 0xFF) / 255F;
		float g = ((piece.color >> 8) & 0xFF) / 255F;
		float b = (piece.color & 0xFF) / 255F;

		boolean hovered = hoveredPiece == piece;

		for(int[] block : piece.blocks)
			renderBlock(stack, block[0], block[1], piece.type, r, g, b, a, hovered);
	}

	private void renderBlock(PoseStack stack, int x, int y, int type, float r, float g, float b, float a, boolean hovered) {
		RenderSystem.setShaderColor(r, g, b, a);
		blit(stack, x * 10, y * 10, 11 + type * 10, imageHeight, 10, 10);
		if(hovered)
			renderHover(stack, x, y);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderHover(PoseStack stack, int x, int y) {
		fill(stack, x * 10, y * 10, x * 10 + 10, y * 10 + 10, 0x66FFFFFF);
	}

	public void add(Button button) {
		send(MatrixEnchantingTableBlockEntity.OPER_ADD, 0, 0, 0);
	}

	public void place(int id, int x, int y) {
		send(MatrixEnchantingTableBlockEntity.OPER_PLACE, id, x, y);
		selectedPiece = -1;
		click();
	}

	public void remove(int id) {
		send(MatrixEnchantingTableBlockEntity.OPER_REMOVE, id, 0, 0);
	}

	public void rotate(int id) {
		send(MatrixEnchantingTableBlockEntity.OPER_ROTATE, id, 0, 0);
	}

	public void merge(int id) {
		int hover = enchanter.matrix.matrix[gridHoverX][gridHoverY];
		Piece p = getPiece(hover);
		Piece p1 = getPiece(selectedPiece);
		if(p != null && p1 != null && p.enchant == p1.enchant && p.level < p.enchant.getMaxLevel()) {
			send(MatrixEnchantingTableBlockEntity.OPER_MERGE, hover, id, 0);
			selectedPiece = -1;
			click();
		}
	}

	private void send(int operation, int arg0, int arg1, int arg2) {
		MatrixEnchanterOperationMessage message = new MatrixEnchanterOperationMessage(operation, arg0, arg1, arg2);
		QuarkNetwork.sendToServer(message);
	}

	private void click() {
		getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	private void updateButtonStatus() {
		plusButton.active = (enchanter.matrix != null
				&& (getMinecraft().player.getAbilities().instabuild || enchanter.charge > 0)
				&& enchanter.matrix.validateXp(getMinecraft().player, enchanter.bookshelfPower)
				&& enchanter.matrix.canGeneratePiece(enchanter.influences, enchanter.bookshelfPower, enchanter.enchantability));
	}

	protected Piece getPiece(int id) {
		EnchantmentMatrix matrix = enchanter.matrix;
		if(matrix != null)
			return matrix.pieces.get(id);

		return null;
	}

}
