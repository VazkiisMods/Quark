package vazkii.quark.integration.lootr.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.client.event.TextureStitchEvent;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.util.Getter;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.render.GenericChestBERenderer;
import vazkii.quark.content.building.module.VariantChestsModule;
import vazkii.quark.integration.lootr.LootrVariantChestBlockEntity;
import vazkii.zeta.event.client.ZPreTextureStitch;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LootrVariantChestRenderer<T extends LootrVariantChestBlockEntity> extends GenericChestBERenderer<T> {
	private UUID playerId = null;
	private static final Map<Block, ChestTextureBatch> chestTextures = new HashMap<>();

	public LootrVariantChestRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public Material getMaterial(T tile, ChestType type) {
		Block block = tile.getBlockState().getBlock();

		ChestTextureBatch batch = chestTextures.get(block);
		if (batch == null)
			return null;

		if (ConfigManager.isVanillaTextures()) {
			return batch.base;
		}

		if (playerId == null) {
			Player player = Getter.getPlayer();
			if (player != null) {
				playerId = player.getUUID();
			} else {
				return batch.unopened;
			}
		}
		if (tile.isOpened()) {
			return batch.opened;
		}
		if (tile.getOpeners().contains(playerId)) {
			return batch.opened;
		} else {
			return batch.unopened;
		}
	}

	public static void accept(ZPreTextureStitch event, Block chest) {
		ResourceLocation atlas = event.getAtlas().location();

		if(chest instanceof VariantChestsModule.IChestTextureProvider prov) {

			String path = prov.getChestTexturePath();
			if (prov.isTrap())
				add(event, atlas, chest, path, "trap", "lootr_trap", "lootr_trap_opened");
			else
				add(event, atlas, chest, path, "normal", "lootr_normal", "lootr_opened");
		}
	}

	private static void add(ZPreTextureStitch event, ResourceLocation atlas, Block chest, String path, String baseSuffix, String unopenedSuffix, String openedSuffix) {
		ResourceLocation resBase = new ResourceLocation(Quark.MOD_ID, path + baseSuffix);
		ResourceLocation resUnopened = new ResourceLocation(Quark.MOD_ID, path + unopenedSuffix);
		ResourceLocation resOpened = new ResourceLocation(Quark.MOD_ID, path + openedSuffix);

		ChestTextureBatch batch = new ChestTextureBatch(atlas, resBase, resUnopened, resOpened);
		chestTextures.put(chest, batch);

		// No need to register base texture, it will have been handled in VariantChestRenderer
		event.addSprite(resUnopened);
		event.addSprite(resOpened);
	}


	private static class ChestTextureBatch {
		public final Material base, unopened, opened;

		public ChestTextureBatch(ResourceLocation atlas, ResourceLocation base, ResourceLocation unopened, ResourceLocation opened) {
			this.base = new Material(atlas, base);
			this.unopened = new Material(atlas, unopened);
			this.opened = new Material(atlas, opened);
		}

	}
}
