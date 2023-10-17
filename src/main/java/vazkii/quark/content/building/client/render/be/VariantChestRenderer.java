package vazkii.quark.content.building.client.render.be;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.client.event.TextureStitchEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.render.GenericChestBERenderer;
import vazkii.quark.content.building.module.VariantChestsModule.IChestTextureProvider;
import vazkii.zeta.event.client.ZPreTextureStitch;

import java.util.HashMap;
import java.util.Map;

public class VariantChestRenderer extends GenericChestBERenderer<ChestBlockEntity> {

	private static final Map<Block, ChestTextureBatch> chestTextures = new HashMap<>();

	public VariantChestRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public Material getMaterial(ChestBlockEntity t, ChestType type) {
		Block block = t.getBlockState().getBlock();

		ChestTextureBatch batch = chestTextures.get(block);
		if(batch == null)
			return null;

		return switch (type) {
			case LEFT -> batch.left;
			case RIGHT -> batch.right;
			default -> batch.normal;
		};
	}

	public static void accept(ZPreTextureStitch event, Block chest) {
		ResourceLocation atlas = event.getAtlas().location();

		if(chest instanceof IChestTextureProvider prov) {

			String path = prov.getChestTexturePath();
			if (prov.isTrap())
				add(event, atlas, chest, path, "trap", "trap_left", "trap_right");
			else
				add(event, atlas, chest, path, "normal", "left", "right");
		}
	}

	private static void add(ZPreTextureStitch event, ResourceLocation atlas, Block chest, String path, String normal, String left, String right) {
		ResourceLocation resNormal = new ResourceLocation(Quark.MOD_ID, path + normal);
		ResourceLocation resLeft = new ResourceLocation(Quark.MOD_ID, path + left);
		ResourceLocation resRight = new ResourceLocation(Quark.MOD_ID, path + right);

		ChestTextureBatch batch = new ChestTextureBatch(atlas, resNormal, resLeft, resRight);
		chestTextures.put(chest, batch);

		event.addSprite(resNormal);
		event.addSprite(resLeft);
		event.addSprite(resRight);
	}

	private static class ChestTextureBatch {
		public final Material normal, left, right;

		public ChestTextureBatch(ResourceLocation atlas, ResourceLocation normal, ResourceLocation left, ResourceLocation right) {
			this.normal = new Material(atlas, normal);
			this.left = new Material(atlas, left);
			this.right = new Material(atlas, right);
		}

	}

}
