package vazkii.quark.addons.oddities.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import vazkii.quark.addons.oddities.block.TinyPotatoBlock;
import vazkii.quark.addons.oddities.block.be.TinyPotatoBlockEntity;
import vazkii.quark.addons.oddities.client.model.TinyPotatoModel;
import vazkii.quark.addons.oddities.client.render.be.TinyPotatoRenderer;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.advancement.QuarkAdvancementHandler;
import vazkii.quark.base.handler.advancement.QuarkGenericTrigger;
import vazkii.quark.base.module.config.Config;
import vazkii.zeta.client.event.ZAddModels;
import vazkii.zeta.client.event.ZClientSetup;
import vazkii.zeta.client.event.ZModelBakingCompleted;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;
import vazkii.zeta.util.Hint;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ZetaLoadModule(category = "oddities", antiOverlap = "botania")
public class TinyPotatoModule extends ZetaModule {

	public static BlockEntityType<TinyPotatoBlockEntity> blockEntityType;
	public static QuarkGenericTrigger patPotatoTrigger;

	@Hint public static Block tiny_potato;

	@Config(description = "Set this to true to use the recipe without the Heart of Diamond, even if the Heart of Diamond is enabled.", flag = "tiny_potato_never_uses_heart")
	public static boolean neverUseHeartOfDiamond = false;

	@LoadEvent
	public final void register(ZRegister event) {
		tiny_potato = new TinyPotatoBlock(this);

		blockEntityType = BlockEntityType.Builder.of(TinyPotatoBlockEntity::new, tiny_potato).build(null);
		Quark.ZETA.registry.register(blockEntityType, "tiny_potato", Registry.BLOCK_ENTITY_TYPE_REGISTRY);

		patPotatoTrigger = QuarkAdvancementHandler.registerGenericTrigger("pat_potato");
	}

	@ZetaLoadModule(clientReplacement = true)
	public static class Client extends TinyPotatoModule {
		@LoadEvent
		public void modelBake(ZModelBakingCompleted event) {
			ResourceLocation tinyPotato = new ModelResourceLocation(new ResourceLocation("quark", "tiny_potato"), "inventory");
			Map<ResourceLocation, BakedModel> map = event.getModels();
			BakedModel originalPotato = map.get(tinyPotato);
			map.put(tinyPotato, new TinyPotatoModel(originalPotato));
		}

		@LoadEvent
		public void registerAdditionalModels(ZAddModels event) {
			ResourceManager rm = Minecraft.getInstance().getResourceManager();
			Set<String> usedNames = new HashSet<>();

			// Register bosnia taters in packs afterwards so that quark overrides for quark tater
			registerTaters(event, "quark", usedNames, rm);
			registerTaters(event, "botania", usedNames, rm);
		}

		private void registerTaters(ZAddModels event, String mod, Set<String> usedNames, ResourceManager rm) {
			Map<ResourceLocation, Resource> resources = rm.listResources("models/tiny_potato", r -> r.getPath().endsWith(".json"));
			for (ResourceLocation model : resources.keySet()) {
				if (mod.equals(model.getNamespace())) {
					String path = model.getPath();
					if ("models/tiny_potato/base.json".equals(path) || usedNames.contains(path))
						continue;

					usedNames.add(path);

					path = path.substring("models/".length(), path.length() - ".json".length());
					event.register(new ResourceLocation("quark", path));
				}
			}
		}

		@LoadEvent
		public final void clientSetup(ZClientSetup event) {
			BlockEntityRenderers.register(blockEntityType, TinyPotatoRenderer::new);
		}
	}
}
