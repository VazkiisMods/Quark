/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 * 
 * File Created @ [18/04/2016, 17:51:13 (GMT)]
 */
package vazkii.quark.base.item;

import net.minecraft.client.renderer.block.statemap.IStateMapper;

public interface IStateMapperProvider {

	public IStateMapper getStateMapper();
	
}
