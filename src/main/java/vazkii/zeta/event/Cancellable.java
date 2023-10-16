package vazkii.zeta.event;

public interface Cancellable {
	boolean isCancelled();
	void cancel();
}
