
package tw.supra.network.request;


public interface NetWorkHandler<T> {
    public enum RequestEvent {
        START,
        PROGRESSING,
        FINISH
    }
    public boolean HandleEvent(RequestEvent event, T info);
}
