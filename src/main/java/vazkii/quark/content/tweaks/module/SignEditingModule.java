package vazkii.quark.content.tweaks.module;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.EditSignMessage;

@LoadModule(category = "tweaks", hasSubscriptions = true)
public class SignEditingModule extends QuarkModule {

	@Hint(key = "sign_editing") TagKey<Item> signs = ItemTags.SIGNS;
	
	@Config public static boolean requiresEmptyHand = false;

	@OnlyIn(Dist.CLIENT)
	public static void openSignGuiClient(BlockPos pos) {
		if(!ModuleLoader.INSTANCE.isModuleEnabled(SignEditingModule.class))
			return;

		Minecraft mc = Minecraft.getInstance();
		BlockEntity tile = mc.level.getBlockEntity(pos);

		if(tile instanceof SignBlockEntity sign)
			mc.player.openTextEdit(sign);
	}

	@SubscribeEvent
	public void onInteract(PlayerInteractEvent.RightClickBlock event) {
		if(event.getUseBlock() == Result.DENY)
			return;

		BlockEntity tile = event.getLevel().getBlockEntity(event.getPos());
		Player player = event.getEntity();
		ItemStack stack = player.getMainHandItem();

		if(player instanceof ServerPlayer serverPlayer
				&& tile instanceof SignBlockEntity sign
				&& !doesSignHaveCommand(sign)
				&& (!requiresEmptyHand || stack.isEmpty())
				&& !(stack.getItem() instanceof DyeItem)
				&& !(stack.getItem() == Items.GLOW_INK_SAC)
				&& !Registry.BLOCK.getKey(tile.getBlockState().getBlock()).getNamespace().equals("signbutton")
				&& player.mayUseItemAt(event.getPos(), event.getFace(), event.getItemStack())
				&& !event.getEntity().isDiscrete()) {

			sign.setAllowedPlayerEditor(player.getUUID());
			sign.isEditable = true;

			QuarkNetwork.sendToPlayer(new EditSignMessage(event.getPos()), serverPlayer);

			event.setCanceled(true);
			event.setCancellationResult(InteractionResult.SUCCESS);
		}
	}

	private boolean doesSignHaveCommand(SignBlockEntity sign) {
		for(Component itextcomponent : sign.messages) {
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
