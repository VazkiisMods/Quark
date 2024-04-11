package org.violetmoon.quark.content.building.module;

import java.util.ArrayList;
import java.util.List;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.building.block.StoolBlock;
import org.violetmoon.quark.content.building.client.render.entity.StoolEntityRenderer;
import org.violetmoon.quark.content.building.entity.Stool;
import org.violetmoon.zeta.client.event.load.ZClientSetup;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.event.play.entity.player.ZRightClickBlock;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.CreativeTabManager;
import org.violetmoon.zeta.util.Hint;
import org.violetmoon.zeta.util.MiscUtil;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

@ZetaLoadModule(category = "building")
public class StoolsModule extends ZetaModule {

	public static EntityType<Stool> stoolEntity;

	@Hint
	TagKey<Item> stoolsTag;

	@LoadEvent
	public final void register(ZRegister event) {
		List<StoolBlock> stools = new ArrayList<>();

		CreativeTabManager.daisyChain();
		for(DyeColor dye : MiscUtil.CREATIVE_COLOR_ORDER)
			stools.add(new StoolBlock(this, dye));
		CreativeTabManager.endDaisyChain();

		CreativeTabManager.daisyChain();
		for(StoolBlock s : stools)
			s.setCreativeTab(CreativeModeTabs.FUNCTIONAL_BLOCKS, Blocks.PINK_BED, false);
		CreativeTabManager.endDaisyChain();

		stoolEntity = EntityType.Builder.of(Stool::new, MobCategory.MISC)
				.sized(6F / 16F, 0.5F)
				.clientTrackingRange(3)
				.updateInterval(Integer.MAX_VALUE) // update interval
				.setShouldReceiveVelocityUpdates(false)
				.setCustomClientFactory((spawnEntity, world) -> new Stool(stoolEntity, world))
				.build("stool");
		Quark.ZETA.registry.register(stoolEntity, "stool", Registries.ENTITY_TYPE);
	}

	@LoadEvent
	public final void setup(ZCommonSetup event) {
		stoolsTag = ItemTags.create(new ResourceLocation(Quark.MOD_ID, "stools"));
	}

	@PlayEvent
	public void itemUsed(ZRightClickBlock event) {
		if(event.getEntity().isShiftKeyDown() && event.getItemStack().getItem() instanceof BlockItem && event.getFace() == Direction.UP) {
			BlockState state = event.getLevel().getBlockState(event.getPos());
			if(state.getBlock() instanceof StoolBlock stool)
				stool.blockClicked(event.getLevel(), event.getPos());
		}
	}

	@LoadEvent
	public final void clientSetup(ZClientSetup event) {
		EntityRenderers.register(stoolEntity, StoolEntityRenderer::new);
	}

}
