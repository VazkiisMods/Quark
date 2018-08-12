package vazkii.quark.base.client.model;

import java.util.List;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import vazkii.quark.decoration.block.BlockCustomFlowerPot;

public class RetexturedModel extends BakedModelWrapper<IBakedModel> {

	private IModel model;
	private final VertexFormat format;
	private final String textureKey;
	public RetexturedModel(IBakedModel originalModel, IModel model, VertexFormat format, String textureKey) {
		super(originalModel);
		this.model = model;
		this.format = format;
		this.textureKey = textureKey;
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		IBakedModel bakedModel = this.originalModel;
		if(state instanceof IExtendedBlockState) {
			IExtendedBlockState extendedState = (IExtendedBlockState) state;
			// TODO: move the property to a common place if using this model for another block
			// needs to be something that exists on both client and server
			String texture = extendedState.getValue(BlockCustomFlowerPot.TEXTURE);
			if(texture != null) {
				IModel retextured = model.retexture(ImmutableMap.of(textureKey, texture));
				bakedModel = retextured.bake(retextured.getDefaultState(), format, ModelLoader.defaultTextureGetter());
			}
		}
		return bakedModel.getQuads(state, side, rand);
	}
}