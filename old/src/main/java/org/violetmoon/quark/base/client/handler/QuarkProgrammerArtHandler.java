package org.violetmoon.quark.base.client.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.config.QuarkGeneralConfig;
import org.violetmoon.zeta.client.event.load.ZClientSetup;
import org.violetmoon.zeta.event.bus.LoadEvent;

public class QuarkProgrammerArtHandler {

	@LoadEvent
	public static void onClientSetup(ZClientSetup event) {
		if(QuarkGeneralConfig.generateProgrammerArt)
			copyProgrammerArtIfMissing();
	}
	
	private static void copyProgrammerArtIfMissing() {
		File dir = new File(".", "resourcepacks");
		File target = new File(dir, "Quark Programmer Art.zip");

		if(!target.exists())
			try {
				dir.mkdirs();
				InputStream in = Quark.class.getResourceAsStream("/assets/quark/programmer_art.zip");
				FileOutputStream out = new FileOutputStream(target);

				byte[] buf = new byte[16384];
				int len;
				while((len = in.read(buf)) > 0)
					out.write(buf, 0, len);

				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
}
