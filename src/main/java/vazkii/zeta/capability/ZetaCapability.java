package vazkii.zeta.capability;

//More of a capability "token". The ID doesn't mean anything idk it might be useful
public record ZetaCapability<T>(String id) {
	@Override
	public boolean equals(Object that) {
		return this == that;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}
}
