package tw.supra.epe.account;

import java.util.ArrayList;
import java.util.HashMap;

import tw.supra.epe.UriDef;
import tw.supra.mod.Identifier;
import tw.supra.mod.ObjIdentifier;
import android.text.TextUtils;

public class UserIdentifier extends ObjIdentifier<User> {
	private static final String LOG_TAG = UserIdentifier.class.getSimpleName();

	public static final String SEGMENT_USER = UriDef.PATH_USER;
	public static final String PARAMETERS_UID = UriDef.PARAMETERS_UID;

	public final String UID;

	public UserIdentifier(String uid) {
		if (TextUtils.isEmpty(uid)) {
			UID = User.ANONYMOUS;
		} else {
			UID = uid;
		}
	}

	@Override
	protected ArrayList<String> defineUriPath() {
		ArrayList<String> path = new ArrayList<String>();
		path.add(SEGMENT_USER);
		return path;
	}

	@Override
	protected HashMap<String, String> defineUriParameters() {
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(PARAMETERS_UID, UID);
		return parameters;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (super.equals(o)) {
			return true;
		}

		if (null != o && o instanceof Identifier) {
			UserIdentifier identifier = (UserIdentifier) o;
			return UID.equals(identifier.UID);
		}
		return false;
	}

	@Override
	public User build() {
		return User.build(UID);
	}

}
