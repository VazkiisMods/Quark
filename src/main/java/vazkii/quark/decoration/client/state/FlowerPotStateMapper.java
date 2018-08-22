package vazkii.quark.decoration.client.state;

import java.util.LinkedHashMap;

import com.google.common.collect.Maps;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;
import vazkii.quark.decoration.block.BlockCustomFlowerPot;

public class FlowerPotStateMapper extends StateMapperBase {
	
	public static final FlowerPotStateMapper INSTANCE = new FlowerPotStateMapper();
	public static final ModelResourceLocation LOCATION = new ModelResourceLocation(new ResourceLocation("quark", "custom_flower_pot"), "normal");

	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		if(state.getValue(BlockCustomFlowerPot.CUSTOM))
			return LOCATION;

		LinkedHashMap<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
		map.remove(BlockCustomFlowerPot.CUSTOM);
		map.remove(BlockCustomFlowerPot.LEGACY_DATA);

		return new ModelResourceLocation(state.getBlock().getRegistryName(), this.getPropertyString(map));
	}
}