package vazkii.quark.experimental.lighting;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;
import vazkii.arl.util.ClientTicker;

public final class ColoredLightSystem {

	private static List<LightSource> lightSources = new ArrayList();
	private static List<LightSource> currentSources = new ArrayList();
	
	private static int lastFrame;
	
	public static void tick(Minecraft mc) {
		ForgeModContainer.forgeLightPipelineEnabled = false;
		mc.gameSettings.ambientOcclusion = 0;

		World world = mc.world;
		if(world == null) {
			lightSources.clear();
			currentSources.clear();
		}
		
		List<LightSource> tempList = new ArrayList(lightSources);
		tempList.removeIf((src) -> src == null || !src.isValid(world));
		currentSources = tempList;
	}
	
	public static float[] getLightColor(IBlockAccess world, BlockPos pos) {
		float totalIncidence = 0;
		float maxBrightness = 0;
		float addR, addG, addB, r, g, b;
		addR = addG = addB = 0;
		
		int time = ClientTicker.ticksInGame;
		if(time != lastFrame)
			prepareFrame();
		lastFrame = time;
		
		for(LightSource src : currentSources) {
			BlockPos srcPos = src.pos;
			IBlockState srcState = world.getBlockState(srcPos);
			Block srcBlock = srcState.getBlock();
			if(!(srcBlock instanceof IColoredLightSource))
				continue;
			
			int srcLight = srcState.getLightValue(world, srcPos);
			float brightness = (float) srcLight / 15F;

			int incidence = src.getIndidence(pos);
			
			if(incidence > 0) {
				float incidenceF = (float) incidence / 15F;
				float negIncidence = 1F - incidenceF;
				float localBrightness = brightness * incidenceF;
				
				float[] colors = ((IColoredLightSource) srcBlock).getColoredLight(world, srcPos);
				if(colors.length != 3)
					colors = new float[] { 1F, 1F, 1F };
				
				maxBrightness = Math.max(maxBrightness, localBrightness);
				
				addR += colors[0] * localBrightness;
				addG += colors[1] * localBrightness;
				addB += colors[2] * localBrightness;
			}
		}
		
		float strongestColor = Math.max(addR, Math.max(addG, addB));
		
		if(maxBrightness > 0 && strongestColor > 0) {
			float lower = 1F - maxBrightness;
			addR /= strongestColor;
			addG /= strongestColor;
			addB /= strongestColor;
			
			addR = MathHelper.clamp(addR, lower, 1F);
			addG = MathHelper.clamp(addG, lower, 1F);
			addB = MathHelper.clamp(addB, lower, 1F);
			
			return new float[] { addR, addG, addB };
		}
		
		return new float[0];
	}
	
	private static void prepareFrame() {
		for(LightSource src : currentSources)
			src.newFrame();
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public static void addLightSource(IBlockAccess access, BlockPos pos, IBlockState state) {
		if(!(access instanceof World))
			return;
		
		World world = (World) access;
		ListIterator<LightSource> iterator = lightSources.listIterator();
		while(iterator.hasNext()) {
			LightSource src = iterator.next();
			if(src.equals(pos)) {
				if(!src.isValid(world))
					iterator.remove();
				else return;
			}
		}
		
		int brightness = state.getBlock().getLightValue(state, access, pos);
		lightSources.add(new LightSource(world, pos, state, brightness));
	}
	
}
