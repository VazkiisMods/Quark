package org.violetmoon.quark.content.tools.module;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.config.Config;
import org.violetmoon.quark.base.item.QuarkArrowItem;
import org.violetmoon.quark.content.tools.client.render.entity.TorchArrowRenderer;
import org.violetmoon.quark.content.tools.entity.TorchArrow;
import org.violetmoon.zeta.client.event.load.ZClientSetup;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.Hint;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;

@ZetaLoadModule(category = "tools")
public class TorchArrowModule extends ZetaModule {

	@Config public static boolean extinguishOnMiss = false;
	
	public static EntityType<TorchArrow> torchArrowType;
	
	@Hint public static Item torch_arrow;

	public static final TagKey<Item> ignoreMultishot = ItemTags.create(new ResourceLocation( "quark:ignore_multishot"));
	
	@LoadEvent
	public final void register(ZRegister event) {
		torch_arrow = new QuarkArrowItem.Impl("torch_arrow", this, (level, stack, living) -> new TorchArrow(level, living));
		
		torchArrowType = EntityType.Builder.<TorchArrow>of(TorchArrow::new, MobCategory.MISC)
				.sized(0.5F, 0.5F)
				.clientTrackingRange(4)
				.updateInterval(20) // update interval
				.build("torch_arrow");
		Quark.ZETA.registry.register(torchArrowType, "torch_arrow", Registry.ENTITY_TYPE_REGISTRY);
	}
	
	@LoadEvent
	public final void clientSetup(ZClientSetup event) {
		EntityRenderers.register(torchArrowType, TorchArrowRenderer::new);
	}
	
}
