package vazkii.quark.base.handler;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.client.ZClientSetup;

import java.util.HashMap;
import java.util.Map;

public class RenderLayerHandler {

	public static void setRenderType(Block block, RenderTypeSkeleton skeleton) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Client.setRenderTypeClient(block, skeleton));
	}

	public static void setInherited(Block block, Block parent) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Client.setInheritedClient(block, parent));
	}

	public enum RenderTypeSkeleton {
		SOLID,
		CUTOUT,
		CUTOUT_MIPPED,
		TRANSLUCENT
	}

	public static class Client {

		private static final Map<Block, RenderTypeSkeleton> mapping = new HashMap<>();
		private static final Map<Block, Block> inheritances = new HashMap<>();
		private static Map<RenderTypeSkeleton, RenderType> renderTypes;

		@LoadEvent
		public static void clientSetup(ZClientSetup event) {
			for(Block b : inheritances.keySet()) {
				Block inherit = inheritances.get(b);
				if(mapping.containsKey(inherit))
					mapping.put(b, mapping.get(inherit));
			}

			for(Block b : mapping.keySet())
				doSetRenderLayer(b, renderTypes.get(mapping.get(b)));

			inheritances.clear();
			mapping.clear();
		}

		//Forge has some weirdo extension, they want you to use json or something.
		//Doing it from java is easier and more akin to how it happens on Fabric.
		@SuppressWarnings("removal")
		private static void doSetRenderLayer(Block block, RenderType type) {
			ItemBlockRenderTypes.setRenderLayer(block, type);
		}

		@OnlyIn(Dist.CLIENT)
		private static void setRenderTypeClient(Block block, RenderTypeSkeleton skeleton) {
			resolveRenderTypes();
			mapping.put(block, skeleton);
		}

		@OnlyIn(Dist.CLIENT)
		private static void setInheritedClient(Block block, Block parent) {
			resolveRenderTypes();
			inheritances.put(block, parent);
		}

		@OnlyIn(Dist.CLIENT)
		private static void resolveRenderTypes() {
			if(renderTypes == null) {
				renderTypes = new HashMap<>();

				renderTypes.put(RenderTypeSkeleton.SOLID, RenderType.solid());
				renderTypes.put(RenderTypeSkeleton.CUTOUT, RenderType.cutout());
				renderTypes.put(RenderTypeSkeleton.CUTOUT_MIPPED, RenderType.cutoutMipped());
				renderTypes.put(RenderTypeSkeleton.TRANSLUCENT, RenderType.translucent());
			}
		}
	}
}
