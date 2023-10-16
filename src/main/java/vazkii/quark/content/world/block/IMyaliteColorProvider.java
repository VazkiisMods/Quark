package vazkii.quark.content.world.block;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.zeta.registry.IZetaBlockColorProvider;

import java.awt.*;

public interface IMyaliteColorProvider extends IZetaBlockColorProvider {

	PerlinSimplexNoise NOISE = new PerlinSimplexNoise(new LegacyRandomSource(4543543),
			ImmutableList.of(-4, -3, -2, -1, 0, 1, 2, 3, 4));

	@Override
	@OnlyIn(Dist.CLIENT)
	default BlockColor getBlockColor() {
		return (state, world, pos, tintIndex) -> getColor(pos, myaliteS(), myaliteB());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	default ItemColor getItemColor() {
		return (stack, tintIndex) -> {
			Minecraft mc = Minecraft.getInstance();
			if(mc.player == null)
				return getColor(BlockPos.ZERO, myaliteS(), myaliteB());

			BlockPos pos = mc.player.blockPosition();
			HitResult res = mc.hitResult;
			if(res != null && res.getType() == HitResult.Type.BLOCK)
				pos = ((BlockHitResult) res).getBlockPos();

			return getColor(pos, myaliteS(), myaliteB());
		};
	}

	default float myaliteS() { return 0.7F; }
	default float myaliteB() { return 0.8F; }

	static int getColor(BlockPos pos, float s, float b) {
		final float sp = 0.15f;
		final double range = 0.3;
		final double shift = 0.05;

		if(pos == null)
			pos = BlockPos.ZERO;

		float x = pos.getX() * sp;
		float y = pos.getY() * sp;
		float z = pos.getZ() * sp;

		double xv = x + Mth.sin(z) * 2;
		double zv = z + Mth.cos(x) * 2;
		double yv = y + Mth.sin(y + Mth.PI / 4) * 2;

		double noise = NOISE.getValue(xv + yv, zv + (yv * 2), false);

		double h = noise * (range / 2) - range + shift;

		return Color.HSBtoRGB((float) h, s, b);
	}

}
