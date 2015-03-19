package tw.supra.epe.activity;

import tw.supra.data.CommonData;
import tw.supra.epe.App;
import tw.supra.epe.core.BaseActivity;
import tw.supra.utils.Log;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue.IdleHandler;
import android.view.KeyEvent;

import com.umeng.analytics.MobclickAgent;
import com.yijiayi.yijiayi.BuildConfig;
import com.yijiayi.yijiayi.R;

public class SplashActivity extends BaseActivity implements IdleHandler {
	private static final String LOG_TAG = SplashActivity.class.getSimpleName();
	private static Handler mHandler = new Handler();

	private static final int TIME_LOCKED = BuildConfig.DEBUG ? 1000 : 1000;// 锁屏时间

	private static final int FLAG_IDLE = 1 << 0;// 阻塞标记 001
	private static final int FLAG_LOCK = 1 << 1;// 时间锁标记 010
	private static final int FLAG_STARTING = 1 << 2;// controler启动中标记100

	private int mStatusFlag = FLAG_LOCK | FLAG_IDLE | FLAG_STARTING;// 状态

	private long mStartAt;

	// Constants
	// The authority for the sync adapter's content provider
	public static final String AUTHORITY = "com.sohu.club.provider";
	// An account type, in the form of a domain name
	public static final String ACCOUNT_TYPE = "club.sohu.com";
	// The account name
	public static final String ACCOUNT = "dummyaccount";
	// Instance fields
	Account mAccount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.splash);
		// setContentView(R.layout.splash);
		mStartAt = System.currentTimeMillis();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				unlockSplash();
			}
		}, TIME_LOCKED);

		// 异步启动
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				Log.i(LOG_TAG, "startUp");
				CommonData.getInstance().getDb();
				App.getInstance().checkUpdateIfNecessary();
				Log.i(LOG_TAG, "startUp ok");
				mStatusFlag &= ~FLAG_STARTING;
				checkPoint("startUp");
				finishSplashIfOk();
				return null;
			}
		}.execute();

		// 注册IdleHandler
		Looper.myQueue().addIdleHandler(this);

		// Create the dummy account
		// mAccount = CreateSyncAccount(this);
	}

	/**
	 * 屏蔽后退键
	 */
	@Override
	public void onBackPressed() {
		// super.onBackPressed();
	}

	/**
	 * 进程脱离阻塞状态的回调
	 */
	@Override
	public boolean queueIdle() {
		Log.i(LOG_TAG, "queueIdle ok");
		mStatusFlag &= ~FLAG_IDLE;
		finishSplashIfOk();
		return false;
	}

	/**
	 * 解锁Splash
	 */
	private void unlockSplash() {
		Log.i(LOG_TAG, "unlock ok");
		mStatusFlag &= ~FLAG_LOCK;
		finishSplashIfOk();
	}

	/**
	 * 检查锁屏状态，如果满足条件将结束Splash进入SohuClubActivity
	 */
	private void finishSplashIfOk() {
		if (checkFlag()) {
			finishSplashAndLaunchMain();
		}
	}

	private void finishSplashAndLaunchMain() {
		// if (BuildConfig.DEBUG
		// &&
		// CommonData.getInstance().getPrefBool(PreferencesCommon.FIRST_LAUNCH,
		// true)) {
		// startActivity(new Intent(this, SetOutActivity.class));
		// CommonData.getInstance().putPrefBool(PreferencesCommon.FIRST_LAUNCH,
		// false);
		// } else {
		// if (BuildConfig.DEBUG) {
		// startActivity(new Intent(IntentAction.ACTION_ACTIVITY_MAIN));
		// } else {
		// startActivity(new Intent(this, SohuClubActivity.class));
		// }
		// }
		// boolean isAnonymousUser = AccountCenter.isAnonymousUser(AccountCenter
		// .getCurrentUserUid());

		// startActivity(new Intent(this,
		// AccountCenter.isLogin() ? MainActivity.class
		// : LoginActivity.class));
		startActivity(new Intent(this, MainActivity.class));
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
		finish();
	}

	/**
	 * 检查是否满足解锁splash的条件
	 * 
	 * @return 满足解锁条件返回true
	 */
	private boolean checkFlag() {
		return (mStatusFlag & FLAG_IDLE) == 0 && (mStatusFlag & FLAG_LOCK) == 0
				&& (mStatusFlag & FLAG_STARTING) == 0;
	}

	/**
	 * 启动计时检查点，打印时间日志
	 * 
	 * @param tag
	 *            : log tag
	 */
	private void checkPoint(String tag) {
		long cost = System.currentTimeMillis() - mStartAt;
		Log.i(LOG_TAG, tag + " : " + cost);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_MENU:
			return true;
		default:
			return super.dispatchKeyEvent(event);
		}
	}

	/**
	 * Create a new dummy account for the sync adapter
	 *
	 * @param context
	 *            The application context
	 */
	public static Account CreateSyncAccount(Context context) {
		// Create the account type and default account
		Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
		// Get an instance of the Android account manager
		AccountManager accountManager = (AccountManager) context
				.getSystemService(ACCOUNT_SERVICE);

		Account[] accounts = accountManager.getAccounts();
		Log.i(LOG_TAG, "accounts : " + accounts.length);
		for (Account account : accounts) {
			Log.i(LOG_TAG, account.name + " by " + account.type);
		}

		/*
		 * Add the account and account type, no password or user data If
		 * successful, return the Account object, otherwise report an error.
		 */
		if (accountManager.addAccountExplicitly(newAccount, null, null)) {
			/*
			 * If you don't set android:syncable="true" in in your <provider>
			 * element in the manifest, then call context.setIsSyncable(account,
			 * AUTHORITY, 1) here.
			 */
		} else {
			/*
			 * The account exists or some other error occurred. Log this, report
			 * it, or handle it internally.
			 */
		}

		// TODO:
		return null;
	}
}
