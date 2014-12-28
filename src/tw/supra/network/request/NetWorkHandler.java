package tw.supra.network.request;

public interface NetWorkHandler<T> {
	public boolean HandleEvent(RequestEvent event, T info);
}
