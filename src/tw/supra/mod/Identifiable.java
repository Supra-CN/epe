package tw.supra.mod;

/**
 * 
 * @author supra
 *
 * @param <T>
 */
public interface Identifiable<T> {
	String getAuthenticatorStr();

	T getAuthenticator();

	public boolean isValid();
}
