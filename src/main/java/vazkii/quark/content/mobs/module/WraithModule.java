package vazkii.quark.content.mobs.module;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.EntityAttributeHandler;
import vazkii.quark.base.handler.advancement.QuarkAdvancementHandler;
import vazkii.quark.base.handler.advancement.mod.MonsterHunterModifier;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.CompoundBiomeConfig;
import vazkii.quark.base.module.config.type.CostSensitiveEntitySpawnConfig;
import vazkii.quark.base.module.config.type.EntitySpawnConfig;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.base.world.EntitySpawnHandler;
import vazkii.quark.content.mobs.client.render.entity.SoulBeadRenderer;
import vazkii.quark.content.mobs.client.render.entity.WraithRenderer;
import vazkii.quark.content.mobs.entity.SoulBead;
import vazkii.quark.content.mobs.entity.Wraith;
import vazkii.quark.content.mobs.item.SoulBeadItem;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.ZConfigChanged;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.client.ZClientSetup;

@LoadModule(category = "mobs")
public class WraithModule extends QuarkModule {

	public static EntityType<Wraith> wraithType;
	public static EntityType<SoulBead> soulBeadType;

	@Config(description = "List of sound sets to use with wraiths.\nThree sounds must be provided per entry, separated by | (in the format idle|hurt|death). Leave blank for no sound (i.e. if a mob has no ambient noise)")
	private static List<String> wraithSounds = Lists.newArrayList(
			"entity.sheep.ambient|entity.sheep.hurt|entity.sheep.death",
			"entity.cow.ambient|entity.cow.hurt|entity.cow.death",
			"entity.pig.ambient|entity.pig.hurt|entity.pig.death",
			"entity.chicken.ambient|entity.chicken.hurt|entity.chicken.death",
			"entity.horse.ambient|entity.horse.hurt|entity.horse.death",
			"entity.cat.ambient|entity.cat.hurt|entity.cat.death",
			"entity.wolf.ambient|entity.wolf.hurt|entity.wolf.death",
			"entity.villager.ambient|entity.villager.hurt|entity.villager.death",
			"entity.polar_bear.ambient|entity.polar_bear.hurt|entity.polar_bear.death",
			"entity.zombie.ambient|entity.zombie.hurt|entity.zombie.death",
			"entity.skeleton.ambient|entity.skeleton.hurt|entity.skeleton.death",
			"entity.spider.ambient|entity.spider.hurt|entity.spider.death",
			"|entity.creeper.hurt|entity.creeper.death",
			"entity.endermen.ambient|entity.endermen.hurt|entity.endermen.death",
			"entity.zombie_pig.ambient|entity.zombie_pig.hurt|entity.zombie_pig.death",
			"entity.witch.ambient|entity.witch.hurt|entity.witch.death",
			"entity.blaze.ambient|entity.blaze.hurt|entity.blaze.death",
			"entity.llama.ambient|entity.llama.hurt|entity.llama.death",
			"|quark:entity.stoneling.cry|quark:entity.stoneling.die",
			"quark:entity.frog.idle|quark:entity.frog.hurt|quark:entity.frog.die"
			);

	@Config
	public static EntitySpawnConfig spawnConfig = new CostSensitiveEntitySpawnConfig(5, 1, 3, 0.7, 0.15, CompoundBiomeConfig.fromBiomeReslocs(false, "minecraft:soul_sand_valley"));

	public static TagKey<Block> wraithSpawnableTag;

	public static TagKey<Structure> soulBeadTargetTag;

	public static List<String> validWraithSounds;
	
	@Hint Item soul_bead;

	@LoadEvent
	public final void register(ZRegister event) {
		soul_bead = new SoulBeadItem(this);

		wraithType = EntityType.Builder.of(Wraith::new, MobCategory.MONSTER)
				.sized(0.6F, 1.95F)
				.clientTrackingRange(8)
				.fireImmune()
				.setCustomClientFactory((spawnEntity, world) -> new Wraith(wraithType, world))
				.build("wraith");
		Quark.ZETA.registry.register(wraithType, "wraith", Registry.ENTITY_TYPE_REGISTRY);

		soulBeadType = EntityType.Builder.of(SoulBead::new, MobCategory.MISC)
				.sized(0F, 0F)
				.clientTrackingRange(4)
				.updateInterval(10) // update frequency
				.fireImmune()
				.setCustomClientFactory((spawnEntity, world) -> new SoulBead(soulBeadType, world))
				.build("soul_bead");
		Quark.ZETA.registry.register(soulBeadType, "soul_bead", Registry.ENTITY_TYPE_REGISTRY);

		EntitySpawnHandler.registerSpawn(wraithType, MobCategory.MONSTER, Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, spawnConfig);
		EntitySpawnHandler.addEgg(this, wraithType, 0xececec, 0xbdbdbd, spawnConfig);

		EntityAttributeHandler.put(wraithType, Wraith::registerAttributes);
		
		QuarkAdvancementHandler.addModifier(new MonsterHunterModifier(this, ImmutableSet.of(wraithType)));
	}

	@LoadEvent
	public final void setup(ZCommonSetup event) {
		wraithSpawnableTag = BlockTags.create(new ResourceLocation(Quark.MOD_ID, "wraith_spawnable"));
		soulBeadTargetTag = TagKey.create(Registry.STRUCTURE_REGISTRY, new ResourceLocation(Quark.MOD_ID, "soul_bead_target"));
	}

	@LoadEvent
	public final void clientSetup(ZClientSetup event) {
		EntityRenderers.register(wraithType, WraithRenderer::new);
		EntityRenderers.register(soulBeadType, SoulBeadRenderer::new);
	}

	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
		validWraithSounds = wraithSounds.stream().filter((s) -> s.split("\\|").length == 3).collect(Collectors.toList());
	}

}
