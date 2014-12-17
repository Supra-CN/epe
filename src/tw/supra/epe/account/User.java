package tw.supra.epe.account;

import java.io.IOException;
import java.util.HashMap;

import tw.supra.data.CommonData;
import tw.supra.data.DBUtils;
import tw.supra.epe.ColumnDef.PrefUser;
import tw.supra.epe.DataDef.DataCommon.TableAccounts;
import tw.supra.epe.DataDef.DataUser;
import tw.supra.mod.PersistableObj;
import tw.supra.utils.EncryptUtil;
import tw.supra.utils.Log;
import tw.supra.utils.MD5;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class User extends PersistableObj {
	private static final String LOG_TAG = User.class.getSimpleName();
	public static final String ANONYMOUS = DataUser.ANONYMOUS;

	public static enum Gender {
		MALE("male"), FEMALE("female"), SECRET("secret"), UNKNOW("");

		public String desc;

		private Gender(String desc) {
			this.desc = desc;
		}

		public static Gender fromDesc(String desc) {
			for (Gender gender : Gender.values()) {
				if (gender.desc.equals(desc)) {
					return gender;
				}
			}
			return UNKNOW;
		}
	}

	private LoginInfo mLoginInfo;

	public final String UID;
	private String mAuth;
	private String mName;

	// private String mTitle;
	// private String mDescription;
	// private String mLastLogin;
	private String mAvatar;
	// private String mProvince;
	private int mScore = -1;

	// private int mGrade = -1;
	// private int mCharm = -1;
	// private int mElite = -1;
	// private int mArt = -1;
	// private int mLineTime = -1;
	// private int mLoginCount = -1;
	// private int mMessageTotal = -1;
	// private int mFriendsTotal = -1;
	// private int mFollowersTotal = -1;
	// private long mRegDate = -1;
	// private Gender mGender = Gender.UNKNOW;

	public static User build(String uid) {
		User user = new User(uid);
		try {
			user.reset();
		} catch (IOException e) {
			user.touch(State.DIRTY);
			e.printStackTrace();
		}
		return user;
	}

	private User(String uid) {
		UID = isLegalNormalUser(uid) ? uid : ANONYMOUS;
		init();
	}

	private void init() {
		if (!UID.equals(getData().getPrefStr(PrefUser.UID, UID))) {
			getData().putPrefStr(PrefUser.UID, UID);
		}
	}

	public void setLoginInfo(LoginInfo info) {
		mLoginInfo = info;
	}

	public LoginInfo getLoginInfo() {
		return mLoginInfo;
	}

	public Cursor queryUserInfo() {
		String table = TableAccounts.NAME;
		String selection = String.format("%s = '%s'",
				TableAccounts.Columns.UID, UID);
		return CommonData.getInstance().getDb()
				.query(table, null, selection, null, null, null, null);
	}

	public UserData getData() {
		return UserData.getUserData(UID);
	}

	public String getPrefStringByKey(String key) {
		String v = getData().getPrefStr(key, "");
		if (TextUtils.isEmpty(v)) {
			v = generateString(key);
			getData().putPrefStr(key, v);
		}
		return v;
	}

	public String getPassProt() {
		return getPrefStringByKey(PrefUser.UID);
	}

	public String getToken() {
		return getPrefStringByKey(PrefUser.TOKEN);
	}

	public String getPPToken() {
		return getPrefStringByKey(PrefUser.PP_TOKEN);
	}

	public String getSIG() {
		// return getPrefStringByKey(PreferencesUser.SIG);
		return generateSIG();
	}

	protected String generateString(String key) {
		if (PrefUser.SIG.equals(key)) {
			return generateSIG();
		} else if (PrefUser.TOKEN.equals(key)) {
			return "";
		} else if (PrefUser.PP_TOKEN.equals(key)) {
			return "";
		} else {
			return "";
		}
	}

	private String generateSIG() {
		String passPort = getPassProt();
		CommonData commonData = CommonData.getInstance();
		String appId = commonData.getAppId();
		String gid = commonData.getGID();
		String md5 = commonData.getPassportMD5();
		return MD5.Md5(passPort + appId + gid + md5);
	}

	public String generateUserTokenAES(String ct) {
		if (AccountCenter.isAnonymousUser(UID)) {
			return "";
		}

		String token = getToken();
		if (TextUtils.isEmpty(token)) {
			return "";
		}

		return EncryptUtil.encryptTokenByAES(token, ct, UID);
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof User && UID.equals(((User) o).UID));
	}

	@Override
	public String toString() {
		return String.format("User: uid = %s,  nickName = %s", UID, getName());
	}

	@Override
	protected void onReset() {
		String selection = String.format("%s = '%s'",
				TableAccounts.Columns.UID, UID);
		Cursor c = CommonData
				.getInstance()
				.getDb()
				.query(TableAccounts.NAME, null, selection, null, null, null,
						null);
		c.moveToFirst();
		bindData(c);
		c.close();
	}

	@Override
	protected void onFlush() {
		String table = TableAccounts.NAME;
		String whereClause = String.format("%s = '%s'",
				TableAccounts.Columns.UID, UID);
		SQLiteDatabase db = CommonData.getInstance().getDb();
		DBUtils.updateOrInsert(db, table, buileValues(), whereClause, null);
	}

	// =====================================================================
	// setter & getter for nomal attrs
	// =====================================================================

	// =====================================================================
	// setter & getter for persistable attrs
	// =====================================================================
	public void setName(String nickName) {
		touch(State.MODIFIED);
		mName = nickName;
	}

	public void setAuth(String auth) {
		touch(State.MODIFIED);
		mAuth = auth;
	}

	public String getAuth() {
		return mAuth;
	}

	public String getName() {
		return mName;
	}

	// public void setTitle(String title) {
	// touch(State.MODIFIED);
	// mTitle = title;
	// }
	//
	// public String getTitle() {
	// return mTitle;
	// }
	//
	// public void setDesc(String description) {
	// touch(State.MODIFIED);
	// mDescription = description;
	// }
	//
	// public String getDesc() {
	// return mDescription;
	// }
	//
	// public void setLastLogin(String lastLogin) {
	// touch(State.MODIFIED);
	// mLastLogin = lastLogin;
	// }
	//
	// public String getLastLogin() {
	// return mLastLogin;
	// }
	//
	public void setAvatarUrl(String avatarUrl) {
		touch(State.MODIFIED);
		mAvatar = avatarUrl;
	}

	public String getAvatarUrl() {
		return mAvatar;
	}

	public void setScore(int score) {
		touch(State.MODIFIED);
		mScore = score;
	}

	public int getScore() {
		return mScore;
	}

	//
	// public void setGrade(int grade) {
	// touch(State.MODIFIED);
	// mGrade = grade;
	// }
	//
	// public int getGrade() {
	// return mGrade;
	// }
	//
	// public void setCharm(int charm) {
	// touch(State.MODIFIED);
	// mCharm = charm;
	// }
	//
	// public int getCharm() {
	// return mCharm;
	// }
	//
	// public void setElite(int elite) {
	// touch(State.MODIFIED);
	// mElite = elite;
	// }
	//
	// public int getElite() {
	// return mElite;
	// }
	//
	// public void setArt(int art) {
	// touch(State.MODIFIED);
	// mArt = art;
	// }
	//
	// public int getArt() {
	// return mArt;
	// }
	//
	// public void setLineTime(int lineTime) {
	// touch(State.MODIFIED);
	// mLineTime = lineTime;
	// }
	//
	// public int getLineTime() {
	// return mLineTime;
	// }
	//
	// public void setLoginCount(int loginCount) {
	// touch(State.MODIFIED);
	// mLoginCount = loginCount;
	// }
	//
	// public int getLoginCount() {
	// return mLoginCount;
	// }
	//
	// public void setProvince(String province) {
	// touch(State.MODIFIED);
	// mProvince = province;
	// }
	//
	// public String getProvince() {
	// return mProvince;
	// }
	//
	// public void setMessageTotal(int messageTotal) {
	// touch(State.MODIFIED);
	// mMessageTotal = messageTotal;
	// }
	//
	// public int getMessageTotal() {
	// return mMessageTotal;
	// }
	//
	// public void setFriendsTotal(int friendsTotal) {
	// touch(State.MODIFIED);
	// mFriendsTotal = friendsTotal;
	// }
	//
	// public int getFriendsTotal() {
	// return mFriendsTotal;
	// }
	//
	// public void setFollowersTotal(int followersTotal) {
	// touch(State.MODIFIED);
	// mFollowersTotal = followersTotal;
	// }
	//
	// public int getFollowersTotal() {
	// return mFollowersTotal;
	// }
	//
	// public void setRegDate(long regDate) {
	// touch(State.MODIFIED);
	// mRegDate = regDate;
	// }
	//
	// public long getRegDate() {
	// return mRegDate;
	// }
	//
	// public void setGender(String genderDesc) {
	// setGender(Gender.fromDesc(genderDesc));
	// }
	//
	// public void setGender(Gender gender) {
	// touch(State.MODIFIED);
	// mGender = gender;
	// }
	//
	// public Gender getGender() {
	// return mGender;
	// }

	// =====================================================================
	// public methods
	// =====================================================================

	// =====================================================================
	// private methods
	// =====================================================================
	private void bindData(Cursor c) {

		if (!DBUtils.checkCursor(c)) {
			Log.e(LOG_TAG, "bindData : bad cursor");
			return;
		}

		setName(DBUtils.getStrByCol(c, TableAccounts.Columns.NAME));
		setAuth(DBUtils.getStrByCol(c, TableAccounts.Columns.AUTH));
		// setNickName(DBUtils.getStrByCol(c, TableAccounts.Columns.NICK_NAME));
		// setTitle(DBUtils.getStrByCol(c, TableAccounts.Columns.TITLE));
		// setDesc(DBUtils.getStrByCol(c, TableAccounts.Columns.DESCRIPTION));
		// setLastLogin(DBUtils.getStrByCol(c,
		// TableAccounts.Columns.LAST_LOGIN));
		setAvatarUrl(DBUtils.getStrByCol(c, TableAccounts.Columns.AVATAR));
		// setGender(DBUtils.getStrByCol(c, TableAccounts.Columns.GENDER));
		setScore(DBUtils.getIntByCol(c, TableAccounts.Columns.SCORE));
		// setGrade(DBUtils.getIntByCol(c, TableAccounts.Columns.GRADE));
		// setCharm(DBUtils.getIntByCol(c, TableAccounts.Columns.CHARM));
		// setElite(DBUtils.getIntByCol(c, TableAccounts.Columns.ELITE));
		// setArt(DBUtils.getIntByCol(c, TableAccounts.Columns.ART));
		// setLineTime(DBUtils.getIntByCol(c, TableAccounts.Columns.LINE_TIME));
		// setLoginCount(DBUtils.getIntByCol(c,
		// TableAccounts.Columns.LOGIN_COUNT));
		// setProvince(DBUtils.getStrByCol(c, TableAccounts.Columns.PROVINCE));
		// setMessageTotal(DBUtils.getIntByCol(c,
		// TableAccounts.Columns.MESSAGE_TOTAL));
		// setFriendsTotal(DBUtils.getIntByCol(c,
		// TableAccounts.Columns.FRIENDS_TOTAL));
		// setFollowersTotal(DBUtils.getIntByCol(c,
		// TableAccounts.Columns.FOLLOWERS_TOTAL));
		// setRegDate(DBUtils.getLongByCol(c, TableAccounts.Columns.REG_DATE));
	}

	private ContentValues buileValues() {
		HashMap<String, String> stringMap = new HashMap<String, String>();
		stringMap.put(TableAccounts.Columns.UID, UID);
		stringMap.put(TableAccounts.Columns.AUTH, getAuth());
		stringMap.put(TableAccounts.Columns.NAME, getName());
		// stringMap.put(TableAccounts.Columns.GENDER, mGender.desc);
		// stringMap.put(TableAccounts.Columns.TITLE, mTitle);
		// stringMap.put(TableAccounts.Columns.DESCRIPTION, mDescription);
		 stringMap.put(TableAccounts.Columns.AVATAR, mAvatar);
		// stringMap.put(TableAccounts.Columns.LAST_LOGIN, mLastLogin);
		// stringMap.put(TableAccounts.Columns.PROVINCE, mProvince);

		HashMap<String, Integer> intMap = new HashMap<String, Integer>();
		intMap.put(TableAccounts.Columns.SCORE, mScore);
		// intMap.put(TableAccounts.Columns.GRADE, mGrade);
		// intMap.put(TableAccounts.Columns.CHARM, mCharm);
		// intMap.put(TableAccounts.Columns.ELITE, mElite);
		// intMap.put(TableAccounts.Columns.ART, mArt);
		// intMap.put(TableAccounts.Columns.LINE_TIME, mLineTime);
		// intMap.put(TableAccounts.Columns.LOGIN_COUNT, mLoginCount);
		// intMap.put(TableAccounts.Columns.MESSAGE_TOTAL, mMessageTotal);
		// intMap.put(TableAccounts.Columns.FRIENDS_TOTAL, mFriendsTotal);
		// intMap.put(TableAccounts.Columns.FOLLOWERS_TOTAL, mFollowersTotal);

		// HashMap<String, Long> longMap = new HashMap<String, Long>();
		// longMap.put(TableAccounts.Columns.REG_DATE, mRegDate);

		ContentValues values = new ContentValues();
		checkAndPutString(values, stringMap);
		checkAndPutInt(values, intMap);
		// checkAndPutLong(values, longMap);

		return values;
	}

	public static boolean isLegalNormalUser(String uid) {
		if (!TextUtils.isEmpty(uid) && !AccountCenter.isAnonymousUser(uid)) {
			return true;
		} else {
			return false;
		}
	}

}
