package vazkii.quark.addons.oddities.module;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.addons.oddities.block.MagnetBlock;
import vazkii.quark.addons.oddities.block.MovingMagnetizedBlock;
import vazkii.quark.addons.oddities.block.be.MagnetBlockEntity;
import vazkii.quark.addons.oddities.block.be.MagnetizedBlockBlockEntity;
import vazkii.quark.addons.oddities.client.render.be.MagnetizedBlockRenderer;
import vazkii.quark.addons.oddities.magnetsystem.MagnetSystem;
import vazkii.quark.api.event.RecipeCrawlEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.client.ZClientSetup;

@LoadModule(category = "oddities", hasSubscriptions = true)
public class MagnetsModule extends QuarkModule {

	public static BlockEntityType<MagnetBlockEntity> magnetType;
	public static BlockEntityType<MagnetizedBlockBlockEntity> magnetizedBlockType;

	@Config(description = "Any items you place in this list will be derived so that any block made of it will become magnetizable")
	public static List<String> magneticDerivationList = Lists.newArrayList("minecraft:iron_ingot", "minecraft:copper_ingot", "minecraft:exposed_copper", "minecraft:weathered_copper", "minecraft:oxidized_copper", "minecraft:raw_iron", "minecraft:raw_copper", "minecraft:iron_ore", "minecraft:deepslate_iron_ore", "minecraft:copper_ore", "minecraft:deepslate_copper_ore");

	@Config public static List<String> magneticWhitelist = Lists.newArrayList("minecraft:chipped_anvil", "minecraft:damaged_anvil");
	@Config public static List<String> magneticBlacklist = Lists.newArrayList("minecraft:tripwire_hook");

	@Config(flag = "magnet_pre_end")  
	public static boolean usePreEndRecipe = false;
	
	@Hint
	public static Block magnet;
	public static Block magnetized_block;

	@LoadEvent
	public final void register(ZRegister event) {
		magnet = new MagnetBlock(this);
		magnetized_block = new MovingMagnetizedBlock(this);

		magnetType = BlockEntityType.Builder.of(MagnetBlockEntity::new, magnet).build(null);
		Quark.ZETA.registry.register(magnetType, "magnet", Registry.BLOCK_ENTITY_TYPE_REGISTRY);

		magnetizedBlockType = BlockEntityType.Builder.of(MagnetizedBlockBlockEntity::new, magnetized_block).build(null);
		Quark.ZETA.registry.register(magnetizedBlockType, "magnetized_block", Registry.BLOCK_ENTITY_TYPE_REGISTRY);
	}

	@LoadEvent
	public final void clientSetup(ZClientSetup event) {
		BlockEntityRenderers.register(magnetizedBlockType, MagnetizedBlockRenderer::new);
	}

	@SubscribeEvent
	public void tick(LevelTickEvent event) {
		MagnetSystem.tick(event.phase == Phase.START, event.level);
	}
	
	@SubscribeEvent
	public void crawlReset(RecipeCrawlEvent.Reset event) {
		MagnetSystem.onRecipeReset();
	}
	
	@SubscribeEvent
	public void crawlDigest(RecipeCrawlEvent.Digest event) {
		MagnetSystem.onDigest();
	}

}
