
package tw.supra.epe.account;

import tw.supra.epe.ApiDef.APIDef;
import tw.supra.network.request.EpeRequestInfo;

import java.util.HashMap;

public class LoginInfo extends EpeRequestInfo {

	public String loginName;
    public String retrunPassport;
    public String passwordMd5;
    public String gid;
    public String sig;
    public String token;
    public String ppToken;

    @Override
    public String toString() {
        return String.format("%s \n  passPort = %s;  passWord = %s; \n  gid = %s \n  sig = %s;",
                super.toString(), loginName, passwordMd5, gid, sig);
    }

    @Override
    public int getRequestMethod() {
        return Method.POST;
    }

    /**
     * 请求URL示例： http://10.11.5.30:8001/?do=nlogin
     * 
     * @return
     */
    @Override
    protected void fillQueryParamters(HashMap<String, String> paramters) {
        paramters.put(APIDef.DO, "nlogin");
    }
}
