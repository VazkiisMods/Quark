package org.violetmoon.quark.integration.lootr.client;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.properties.ChestType;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.building.module.VariantChestsModule;
import org.violetmoon.quark.integration.lootr.LootrVariantChestBlockEntity;

import java.util.UUID;

import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.util.Getter;

public class LootrVariantChestRenderer<T extends LootrVariantChestBlockEntity> extends ChestRenderer<T> {

	private UUID playerIdCache = null;
	protected final boolean isTrap;

	public LootrVariantChestRenderer(BlockEntityRendererProvider.Context context, boolean isTrap) {
		super(context);
		this.isTrap = isTrap;
	}

	@Override
	public Material getMaterial(T tile, ChestType type) {
		if(!(tile.getBlockState().getBlock() instanceof VariantChestsModule.IVariantChest v))
			return null;

		//lazy-init pattern
		if(playerIdCache == null) {
			Player player = Getter.getPlayer();
			if(player != null)
				playerIdCache = player.getUUID();
		}

		boolean opened = tile.isOpened() || tile.getOpeners().contains(playerIdCache);

		//apply the texture naming convention
		StringBuilder tex = new StringBuilder(v.getTextureFolder())
				.append('/')
				.append(v.getTexturePath())
				.append('/');
		if(isTrap) {
			if(ConfigManager.isVanillaTextures())
				tex.append("trap");
			else if(opened)
				tex.append("lootr_trap_opened");
			else
				tex.append("lootr_trap");
		} else {
			if(ConfigManager.isVanillaTextures())
				tex.append("normal");
			else if(opened)
				tex.append("lootr_opened");
			else
				tex.append("lootr_normal");
		}

		return new Material(Sheets.CHEST_SHEET, new ResourceLocation(Quark.MOD_ID, tex.toString()));
	}
}
