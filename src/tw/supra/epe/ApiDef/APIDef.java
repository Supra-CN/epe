package tw.supra.epe.ApiDef;

import com.yijiayi.yijiayi.BuildConfig;


public class APIDef {
	public static final int API_VERSION = 1000;

//	public static final int THREAD_TYPE_NORMAL = 0;
//	public static final int THREAD_TYPE_PHOTO = 1;
//	public static final int THREAD_TYPE_RESREACH = 2;

//	public static final String[] HOSTS_SCALABLE_IMAGE = { "image6.club.sohu.net", "image7.club.sohu.net" };

	// public static final String HOST_CLUB_FACE = "club.sohu.com";
//	public static final String HOST_CLUB_FACE = "club.sohu";
//	public static final String HOST_SOHU_VIDEO = "share.vrs.sohu.com";

	public static final String SCHEMA = "http";

	public static final String HOST_API_RELEASE = "www.alayy.com";
	public static final String HOST_API_DEBUG = HOST_API_RELEASE;
//	 public static final String HOST_API_DEBUG = "182.92.158.32";

	public static final String HOST_API = BuildConfig.DEBUG ? HOST_API_DEBUG : HOST_API_RELEASE;// 接口

	public static final String DO = "do";
	public static final String CLIENT_ANDROID = "android";
	public static final String CLIENT_IOS = "ios";
	public static final String API_VER = "1";

	public static final String KEY_ERROR_CODE = "errorcode";
	public static final String KEY_ERROR_DESC = "msg";
	public static final String KEY_TIME_STAMP = "timeStamp";

//	public static final String HOST_PP = "passport.sohu.com";

//	public static final String URL_AGREEMENT = "http://m.passport.sohu.com/agreement";
//	public static final String URL_FORGOT_PASSWORD = "http://m.passport.sohu.com/f ";

}
