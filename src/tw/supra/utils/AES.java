package tw.supra.utils;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {


	/**
	 * 填充字符串,总长为16的倍数,AES加密算法要求 -- 此方法设为私有即可
	 * @param str
	 * @return
	 */
	private static String paddingString(String str) {
		int len = str.getBytes().length;

		if (0 == len % 16) {
			return str;
		}
		byte[] bytes = new byte[(16 * (len / 16)) + 16];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = '\0';
		}
		try {
			byte[] strbytes = str.getBytes("UTF-8");
			for (int i = 0; i < len; i++) {
				bytes[i] = strbytes[i];
			}
			return new String(bytes);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 去除填充字符串
	 * @param str
	 * @return
	 */
	private static String dePaddingString(String str) {
		int len = str.getBytes().length;
		int pos = 0;
		byte[] strbytes;
		try {
			strbytes = str.getBytes("UTF-8");
			for (int i = 0; i < len; i++) {
				if((strbytes[i]) == '\0') {
					pos = i;
					break;
				}
			}
			if(pos != 0) {
				byte[]destByte = new byte[pos];
				System.arraycopy(strbytes,0, destByte,0, pos);
				return new String(destByte);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}
	/**
	 * 加密
	 * @param sSrc
	 * @param sKey 16位字符串
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(String sSrc, String sKey) throws Exception {
		if (sKey == null) {
			return null;
		}
		
		if (sKey.length() != 16) {
			return null;
		}
		
		String encryptstr = paddingString(sSrc);
		
		byte[] raw = sKey.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(encryptstr.getBytes());

		return encrypted;
	}

	/**
	 * 解密
	 * @param sSrc
	 * @param sKey
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(byte[] sSrc, String sKey) throws Exception {
		try {
			if (sKey == null) {
				return null;
			}
			if (sKey.length() != 16) {
				return null;
			}
			byte[] raw = sKey.getBytes("UTF-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			try {
				byte[] original = cipher.doFinal(sSrc);
				return dePaddingString(new String(original));

			} catch (Exception e) {
				return null;
			}
		} catch (Exception ex) {
			return null;
		}
	}
}
