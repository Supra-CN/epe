package tw.supra.epe.account;

import android.text.TextUtils;

import tw.supra.epe.UriDef;
import tw.supra.mod.ObjIdentifier;
import tw.supra.mod.Identifier;

import java.util.ArrayList;
import java.util.HashMap;

public class UserIdentifier extends ObjIdentifier<User> {
	private static final String LOG_TAG = UserIdentifier.class.getSimpleName();

	public static final String SEGMENT_USER = UriDef.PATH_USER;
	public static final String PARAMETERS_PASSPORT = UriDef.PARAMETERS_PASSPORT;

	public final String PASSPORT;

	public UserIdentifier(String passport) {
		if (TextUtils.isEmpty(passport)) {
			PASSPORT = User.ANONYMOUS;
		} else {
			PASSPORT = passport;
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
		parameters.put(PARAMETERS_PASSPORT, PASSPORT);
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
			return PASSPORT.equals(identifier.PASSPORT);
		}
		return false;
	}

	@Override
	public User build() {
		return User.build(PASSPORT);
	}

}
