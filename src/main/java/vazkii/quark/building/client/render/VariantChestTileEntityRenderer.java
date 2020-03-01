package vazkii.quark.building.client.render;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.GenericChestTERenderer;
import vazkii.quark.building.module.VariantChestsModule.IChestTextureProvider;

public class VariantChestTileEntityRenderer extends GenericChestTERenderer<ChestTileEntity> {

	private static Map<Block, ChestTextureBatch> chestTextures = new HashMap<>();
	
	public static Block invBlock = null; 

	public VariantChestTileEntityRenderer(TileEntityRendererDispatcher disp) {
		super(disp);
	}

	@Override
	public Material getMaterial(ChestTileEntity t, ChestType type) {
		Block block = invBlock;
		if(block == null)
			block = t.getBlockState().getBlock();
		
		ChestTextureBatch batch = chestTextures.get(block);
		
		switch(type) {
		case LEFT: return batch.left;
		case RIGHT: return batch.right;
		default: return batch.normal;
		}
	}

	public static void accept(TextureStitchEvent.Pre event, Block chest) {
		ResourceLocation atlas = event.getMap().getTextureLocation();

		if(chest instanceof IChestTextureProvider) {
			IChestTextureProvider prov = (IChestTextureProvider) chest;

			String path = prov.getChestTexturePath();
			if(!prov.isTrap())
				add(event, atlas, chest, path, "normal", "left", "right");
			else
				add(event, atlas, chest, path, "trap", "trap_left", "trap_right");
		}
	}

	private static void add(TextureStitchEvent.Pre event, ResourceLocation atlas, Block chest, String path, String normal, String left, String right) {
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
