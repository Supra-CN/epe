package tw.supra.epe.account;

import tw.supra.data.CommonData;
import tw.supra.epe.ColumnDef.PrefCommon;
import tw.supra.mod.ModelManager;

public class AccountCenter {
	private static final String LOG_TAG = AccountCenter.class.getSimpleName();

	private AccountCenter() {
	}

	public static void switchUser(String uid) {
		if (isCurrentUser(uid)) {
			return;
		}
		CommonData.getInstance().putPrefStr(PrefCommon.CURRENT_USER, uid);
		initCurrentUser();
	}

	/**
	 * 初始化当前用户
	 */
	public static void initCurrentUser() {
		getCurrentUser();
	}

	public static String getCurrentUserUid() {
		return CommonData.getInstance().getPrefStr(PrefCommon.CURRENT_USER,
				User.ANONYMOUS);
	}

	public static User getCurrentUser() {
		return getUser(getCurrentUserUid());
	}

	/**
	 * 获得用户实例
	 * 
	 * @param uid
	 * @return
	 */
	public static User getUser(String uid) {
		return ModelManager.getInstance().getObj(new UserIdentifier(uid));
	}

	public static UserData getUserData(String uid) {
		return UserData.getUserData(uid);
	}

	public static Boolean isCurrentUser(String uid) {
		return getCurrentUserUid().equals(uid);
	}

	public static Boolean isAnonymousUser(String uid) {
		return User.ANONYMOUS.equals(uid);
	}

	public static boolean isLogin() {
		return !isCurrentUser(User.ANONYMOUS);
	}

}
