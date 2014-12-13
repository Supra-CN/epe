
package tw.supra.epe;

import android.app.Application;

import com.umeng.analytics.MobclickAgent;

public class App extends Application {
    public static final String ACTION_LOGIN = "com.sohu.club.intent.action.ACTION_LOGIN";
    private static App sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        MobclickAgent.setDebugMode(BuildConfig.DEBUG);
    }

    public static App getInstance() {
        return sInstance;
    }
}
