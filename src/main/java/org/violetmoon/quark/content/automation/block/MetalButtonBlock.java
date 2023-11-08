package org.violetmoon.quark.content.automation.block;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import org.violetmoon.quark.base.block.QuarkButtonBlock;
import org.violetmoon.zeta.module.ZetaModule;

import javax.annotation.Nonnull;

/**
 * @author WireSegal
 * Created at 9:14 PM on 10/8/19.
 */
public class MetalButtonBlock extends QuarkButtonBlock {

	private final int speed;

	public MetalButtonBlock(String regname, ZetaModule module, int speed) {
		super(regname, module, CreativeModeTab.TAB_REDSTONE,
				Block.Properties.of(Material.DECORATION)
						.noCollission()
						.strength(0.5F)
						.sound(SoundType.METAL));
		this.speed = speed;
	}

	@Override
	public int getPressDuration() {
		return speed;
	}

	@Nonnull
	@Override
	protected SoundEvent getSound(boolean powered) {
		return powered ? SoundEvents.STONE_BUTTON_CLICK_ON : SoundEvents.STONE_BUTTON_CLICK_OFF;
	}
}
