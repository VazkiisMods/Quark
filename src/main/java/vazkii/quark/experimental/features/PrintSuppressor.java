/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [23/06/2016, 17:43:40 (GMT)]
 */
package vazkii.quark.experimental.features;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.function.Consumer;

import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;

public class PrintSuppressor extends Feature {

	boolean suppressOut, suppressErr;

	@Override
	public void setupConfig() {
		suppressOut = loadPropBool("Suppress STDOUT", "", true);
		suppressErr = loadPropBool("Suppress STDERR", "", false);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		LibMisc.LOGGER.warn("Quark print suppression is enabled. Important info might be missing. Suppressing STDOUT={}, STDERR={}", suppressOut, suppressErr);

		if(suppressOut)
			oppressFreedomOfSpeech(System::setOut);
		if(suppressErr)
			oppressFreedomOfSpeech(System::setErr);
	}

	private void oppressFreedomOfSpeech(Consumer<PrintStream> consumer) {
		PrintStream oppressedStream = new PrintStream(new OutputStream() {
			@Override public void write(int b) throws IOException {
				// NO-OP
			}
		});

		consumer.accept(oppressedStream);
	}

	@Override
	public String getFeatureDescription() {
		return "Suppresses all STDOUT (and STDERR, if enabled) messages so they don't show up in the console."
				+ "\nUse this if some mod left behind debug messages and you don't want to see them."
				+ "\nIf important messages end up being disabled by this, tell the modders in case to switch to a proper logger.";
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
