package vazkii.quark.decoration.client.state;

import java.util.LinkedHashMap;

import com.google.common.collect.Maps;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.decoration.block.BlockCustomFlowerPot;

@SideOnly(Side.CLIENT)
public class ColoredFlowerPotStateMapper extends StateMapperBase {
	
	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		ResourceLocation loc = state.getBlock().getRegistryName();
		if(state.getValue(BlockCustomFlowerPot.CUSTOM))
			return new ModelResourceLocation(loc, "contents=custom");

		LinkedHashMap<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
		map.remove(BlockCustomFlowerPot.CUSTOM);
		map.remove(BlockCustomFlowerPot.LEGACY_DATA);

		return new ModelResourceLocation(loc, this.getPropertyString(map));
	}
}