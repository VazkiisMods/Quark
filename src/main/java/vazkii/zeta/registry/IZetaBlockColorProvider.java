package vazkii.zeta.registry;

import net.minecraft.client.color.block.BlockColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IZetaBlockColorProvider extends IZetaItemColorProvider {

	@OnlyIn(Dist.CLIENT)
	public BlockColor getBlockColor();

}