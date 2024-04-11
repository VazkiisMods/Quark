package org.violetmoon.quark.content.building.block;

import org.jetbrains.annotations.Nullable;

import org.violetmoon.zeta.block.ZetaPillarBlock;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.IZetaBlockColorProvider;

public class MyalitePillarBlock extends ZetaPillarBlock implements IZetaBlockColorProvider {

	public MyalitePillarBlock(String regname, @Nullable ZetaModule module, Properties properties) {
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
