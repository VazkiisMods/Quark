package org.violetmoon.quark.addons.oddities.module;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.violetmoon.quark.addons.oddities.block.MagnetBlock;
import org.violetmoon.quark.addons.oddities.block.MovingMagnetizedBlock;
import org.violetmoon.quark.addons.oddities.block.be.MagnetBlockEntity;
import org.violetmoon.quark.addons.oddities.block.be.MagnetizedBlockBlockEntity;
import org.violetmoon.quark.addons.oddities.client.render.be.MagnetizedBlockRenderer;
import org.violetmoon.quark.addons.oddities.magnetsystem.MagnetSystem;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.zeta.client.event.load.ZClientSetup;
import org.violetmoon.zeta.config.Config;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.event.play.ZLevelTick;
import org.violetmoon.zeta.event.play.ZRecipeCrawl;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.Hint;
import org.violetmoon.zeta.util.handler.ToolInteractionHandler;

import java.util.List;

@ZetaLoadModule(category = "oddities")
public class MagnetsModule extends ZetaModule {

	public static BlockEntityType<MagnetBlockEntity> magnetType;
	public static BlockEntityType<MagnetizedBlockBlockEntity> magnetizedBlockType;

	@Config(description = "Any items you place in this list will be derived so that any block made of it will become magnetizable")
	public static List<String> magneticDerivationList = Lists.newArrayList("minecraft:iron_ingot", "minecraft:copper_ingot", "minecraft:exposed_copper", "minecraft:weathered_copper", "minecraft:oxidized_copper", "minecraft:raw_iron", "minecraft:raw_copper", "minecraft:iron_ore", "minecraft:deepslate_iron_ore", "minecraft:copper_ore", "minecraft:deepslate_copper_ore", "quark:gravisand");

	@Config(description = "Block IDs to force-allow magnetism on, regardless of their crafting recipe")
	public static List<String> magneticWhitelist = Lists.newArrayList("minecraft:chipped_anvil", "minecraft:damaged_anvil");

	@Config(description = "Block IDs to force-disable magnetism on, regardless of their crafting recipe")
	public static List<String> magneticBlacklist = Lists.newArrayList("minecraft:tripwire_hook");

	@Config(flag = "magnet_pre_end")
	public static boolean usePreEndRecipe = false;

	@Hint
	public static Block magnet;
	public static Block magnetized_block;

	@LoadEvent
	public final void register(ZRegister event) {
		magnet = new MagnetBlock(this);
		magnetized_block = new MovingMagnetizedBlock(this);

		ToolInteractionHandler.registerWaxedBlockBooleanProperty(this, magnet, MagnetBlock.WAXED);

		magnetType = BlockEntityType.Builder.of(MagnetBlockEntity::new, magnet).build(null);
		Quark.ZETA.registry.register(magnetType, "magnet", Registries.BLOCK_ENTITY_TYPE);

		magnetizedBlockType = BlockEntityType.Builder.of(MagnetizedBlockBlockEntity::new, magnetized_block).build(null);
		Quark.ZETA.registry.register(magnetizedBlockType, "magnetized_block", Registries.BLOCK_ENTITY_TYPE);
	}

	@LoadEvent
	public final void clientSetup(ZClientSetup event) {
		BlockEntityRenderers.register(magnetizedBlockType, MagnetizedBlockRenderer::new);
	}

	@PlayEvent
	public void tickStart(ZLevelTick.Start event) {
		MagnetSystem.tick(true, event.getLevel());
	}

	@PlayEvent
	public void tickEnd(ZLevelTick.End event) {
		MagnetSystem.tick(false, event.getLevel());
	}

	@PlayEvent
	public void crawlReset(ZRecipeCrawl.Reset event) {
		MagnetSystem.onRecipeReset();
	}

	@PlayEvent
	public void crawlDigest(ZRecipeCrawl.Digest event) {
		MagnetSystem.onDigest();
	}

}
