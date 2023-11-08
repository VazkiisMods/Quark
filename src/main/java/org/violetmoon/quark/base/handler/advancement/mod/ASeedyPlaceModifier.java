package org.violetmoon.quark.base.handler.advancement.mod;

import com.google.common.collect.ImmutableSet;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.PlacedBlockTrigger;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.violetmoon.quark.api.IMutableAdvancement;
import org.violetmoon.quark.base.handler.advancement.AdvancementModifier;
import org.violetmoon.zeta.module.ZetaModule;

import java.util.Set;

public class ASeedyPlaceModifier extends AdvancementModifier {

	private static final ResourceLocation TARGET = new ResourceLocation("husbandry/plant_seed");

	final Set<Block> seeds;

	public ASeedyPlaceModifier(ZetaModule module, Set<Block> seeds) {
		super(module);
		this.seeds = seeds;

	}

	@Override
	public Set<ResourceLocation> getTargets() {
		return ImmutableSet.of(TARGET);
	}

	@Override
	public boolean apply(ResourceLocation res, IMutableAdvancement adv) {
		for(var block : seeds) {
			Criterion criterion = new Criterion(PlacedBlockTrigger.TriggerInstance.placedBlock(block));
			
			String name = Registry.BLOCK.getKey(block).toString();
			adv.addOrCriterion(name, criterion);
		}
		
		return true;
	}

}
