package vazkii.zeta.module;

import java.util.function.Supplier;
import java.util.stream.Stream;

public interface ModuleFinder extends Supplier<Stream<ZetaLoadModuleAnnotationData>> {
	default ModuleFinder and(ModuleFinder other) {
		return () -> Stream.concat(this.get(), other.get());
	}
}
