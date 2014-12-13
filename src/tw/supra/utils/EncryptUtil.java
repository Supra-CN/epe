package tw.supra.utils;

import android.util.Base64;

public class EncryptUtil {
	private static final String LOG_TAG = EncryptUtil.class.getSimpleName();

	public static String encryptTokenByAES(String token, String ct,
			String userName) {
		try {
			String aesKey = MD5.Md5(ct + userName).substring(0, 16);
			byte[] tokenAes = AES.encrypt(token, aesKey);
			byte[] tokenAesBase64 = Base64.encode(tokenAes, Base64.DEFAULT);
			String tokenAesBase64Str = new String(tokenAesBase64);
			return tokenAesBase64Str;
		} catch (Exception e) {
			Log.i(LOG_TAG, "Exception : " + e);
			e.printStackTrace();
			return "";
		}

	}
}
