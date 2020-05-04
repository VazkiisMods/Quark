package vazkii.quark.tweaks.module;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.Config;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.EditSignMessage;

@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class SignEditingModule extends Module {

	@Config public static boolean requiresEmptyHand = false;

	@OnlyIn(Dist.CLIENT)
	public static void openSignGuiClient(BlockPos pos) {
		if(!ModuleLoader.INSTANCE.isModuleEnabled(SignEditingModule.class))
			return;

		Minecraft mc = Minecraft.getInstance();
		TileEntity tile = mc.world.getTileEntity(pos);

		if(tile instanceof SignTileEntity)
			mc.player.openSignEditor((SignTileEntity) tile);
	}

	@SubscribeEvent
	public void onInteract(PlayerInteractEvent.RightClickBlock event) {
		if(event.getUseBlock() == Result.DENY)
			return;	

		TileEntity tile = event.getWorld().getTileEntity(event.getPos());
		PlayerEntity player = event.getPlayer();
		ItemStack stack = player.getHeldItemMainhand();

		if(player instanceof ServerPlayerEntity 
				&& tile instanceof SignTileEntity 
				&& !doesSignHaveCommand((SignTileEntity) tile)
				&& (!requiresEmptyHand || stack.isEmpty()) 
				&& !(stack.getItem() instanceof DyeItem) 
				&& player.canPlayerEdit(event.getPos(), event.getFace(), event.getItemStack()) 
				&& !event.getEntity().isSneaking()) {

			SignTileEntity sign = (SignTileEntity) tile;
			sign.setPlayer(player);
			sign.setEditable(true);

			QuarkNetwork.sendToPlayer(new EditSignMessage(event.getPos()), (ServerPlayerEntity) player);
		}
	}

	private boolean doesSignHaveCommand(SignTileEntity sign) {
		for(ITextComponent itextcomponent : sign.signText) {
			Style style = itextcomponent == null ? null : itextcomponent.getStyle();
			if (style != null && style.getClickEvent() != null) {
				ClickEvent clickevent = style.getClickEvent();
				if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
					return true;
				}
			}
		}

		return false;
	}

}
