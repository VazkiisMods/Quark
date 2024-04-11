package org.violetmoon.quark.content.world.block;

import org.jetbrains.annotations.Nullable;

import org.violetmoon.zeta.block.ZetaBlock;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.IZetaBlockColorProvider;

public class MyaliteBlock extends ZetaBlock implements IZetaBlockColorProvider {

	public MyaliteBlock(String regname, @Nullable ZetaModule module, Properties properties) {
		super(regname, module, properties);
	}

	@Override
	public @Nullable String getBlockColorProviderName() {
		return "myalite";
	}

	@Override
	public @Nullable String getItemColorProviderName() {
		return "myalite";
	}

}
