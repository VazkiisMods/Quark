package vazkii.quark.base.handler.advancement.mod;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.api.IMutableAdvancement;
import vazkii.quark.base.handler.advancement.AdvancementModifier;
import vazkii.quark.base.module.QuarkModule;

public class MonsterHunterModifier extends AdvancementModifier {

	private static final ResourceLocation TARGET_ONE = new ResourceLocation("adventure/kill_a_mob");
	private static final ResourceLocation TARGET_ALL = new ResourceLocation("adventure/kill_all_mobs");
	
	final Set<EntityType<?>> entityTypes;
	
	public MonsterHunterModifier(QuarkModule module, Set<EntityType<?>> entityTypes) {
		super(module);
		
		this.entityTypes = entityTypes;
	}

	@Override
	public Set<ResourceLocation> getTargets() {
		return ImmutableSet.of(TARGET_ONE, TARGET_ALL);
	}

	@Override
	public boolean apply(ResourceLocation res, IMutableAdvancement adv) {
		boolean all = res.equals(TARGET_ALL);
		
		for(EntityType<?> type : entityTypes) {
			Criterion criterion = new Criterion(KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(type))));
			
			String name = ForgeRegistries.ENTITY_TYPES.getKey(type).toString();
			if(all)
				adv.addRequiredCriterion(name, criterion);
			else adv.addOrCriterion(name, criterion);
		}
		
		return true;
	}

}
