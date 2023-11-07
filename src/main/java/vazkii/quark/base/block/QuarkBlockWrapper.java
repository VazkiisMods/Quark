package vazkii.quark.base.block;

import java.util.function.BooleanSupplier;

import net.minecraft.world.level.block.Block;
import vazkii.zeta.module.ZetaModule;

// Wrapper to allow vanilla blocks to be treated as quark blocks contextualized under a module
public class QuarkBlockWrapper implements IQuarkBlock {

	private final Block parent;
	private final ZetaModule module;
	
	private BooleanSupplier condition;
	
	public QuarkBlockWrapper(Block parent, ZetaModule module) {
		this.parent = parent;
		this.module = module;
	}
	
	@Override
	public Block getBlock() {
		return parent;
	}

	@Override
	public ZetaModule getModule() {
		return module;
	}

	@Override
	public QuarkBlockWrapper setCondition(BooleanSupplier condition) {
		this.condition = condition;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return condition == null || condition.getAsBoolean();
	}

}
