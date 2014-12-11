
package tw.supra.epe.account;

import android.text.TextUtils;

public class AccountUtils {
    public static boolean isLegalPassport(String passport) {
        if (TextUtils.isEmpty(passport)) {
            return false;
        } else if (passport.contains(" ")) {
            return false;
        }
        return true;
    }
    public static boolean isLegalUserName(String userName) {
        if (TextUtils.isEmpty(userName)) {
            return false;
        } else if (userName.contains(" ")) {
            return false;
        }
        return true;
    }
    
    public static boolean isLegalPhoneNum(String phoneNum) {
        if (TextUtils.isEmpty(phoneNum)) {
            return false;
        } else if (phoneNum.contains(" ")) {
            return false;
        }else if (! TextUtils.isDigitsOnly(phoneNum)) {
            return false;
        }else if(phoneNum.length() < 10){
            return false;
        }
        return true;
    }
    public static boolean isLegalPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            return false;
        } else if (password.contains(" ")) {
            return false;
        }
        return true;
    }
}
