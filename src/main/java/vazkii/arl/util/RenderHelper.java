/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 * File Created @ [Jan 19, 2014, 5:40:38 PM (GMT)]
 */
package vazkii.arl.util;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class RenderHelper {

	public static String getKeyDisplayString(String keyName) {
		String key = null;
		KeyMapping[] keys = Minecraft.getInstance().options.keyMappings;
		for(KeyMapping otherKey : keys)
			if(otherKey.getName().equals(keyName)) {
				key = otherKey.saveString();
				break;
			}

		return I18n.get(key);
	}
}