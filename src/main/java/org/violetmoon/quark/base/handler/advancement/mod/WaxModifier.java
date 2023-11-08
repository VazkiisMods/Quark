package org.violetmoon.quark.base.handler.advancement.mod;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ItemInteractWithBlockTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.violetmoon.quark.api.IMutableAdvancement;
import org.violetmoon.quark.base.handler.advancement.AdvancementModifier;
import org.violetmoon.zeta.module.ZetaModule;

import java.util.HashSet;
import java.util.Set;

public class WaxModifier  extends AdvancementModifier {

	private static final ResourceLocation TARGET_ON = new ResourceLocation("husbandry/wax_on");
	private static final ResourceLocation TARGET_OFF = new ResourceLocation("husbandry/wax_off");
	
	private final Set<Block> unwaxed;
	private final Set<Block> waxed;
	
	public WaxModifier(ZetaModule module, Set<Block> unwaxed, Set<Block> waxed) {
		super(module);
		
		this.unwaxed = unwaxed;
		this.waxed = waxed;

		Preconditions.checkArgument(!unwaxed.isEmpty() || !waxed.isEmpty(), "Advancement modifier list cant be empty");

	}

	@Override
	public Set<ResourceLocation> getTargets() {
		return ImmutableSet.of(TARGET_ON, TARGET_OFF);
	}

	@Override
	public boolean apply(ResourceLocation res, IMutableAdvancement adv) {
		String title = res.getPath().replaceAll(".+/", "");
		Criterion criterion = adv.getCriterion(title);
		if(criterion != null && criterion.getTrigger() instanceof ItemInteractWithBlockTrigger.TriggerInstance iib) {
			Set<Block> blockSet = iib.location.block.blocks;
			if(blockSet != null) {
				Set<Block> ourSet = res.equals(TARGET_ON) ? unwaxed : waxed;

				if (!addToBlockSet(blockSet, ourSet)) {
					blockSet = new HashSet<>(blockSet);
					iib.location.block.blocks = blockSet;
					addToBlockSet(blockSet, ourSet);
				}
			}
		}
		
		return true;
	}
	
	private static boolean addToBlockSet(Set<Block> blockSet, Set<Block> ourSet) {
		try {
			blockSet.addAll(ourSet);
		} catch(Exception e) {
			return false;
		}
		
		return true;
	}

}
