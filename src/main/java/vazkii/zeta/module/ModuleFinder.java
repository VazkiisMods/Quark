package vazkii.zeta.module;

import java.util.function.Supplier;
import java.util.stream.Stream;

public interface ModuleFinder extends Supplier<Stream<? extends TentativeModule>> { }
