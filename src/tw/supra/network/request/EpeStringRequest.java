
package tw.supra.network.request;

import java.io.UnsupportedEncodingException;

import tw.supra.epe.ApiDef.EpeErrorCode;
import tw.supra.network.NetworkResponse;
import tw.supra.network.toolbox.HttpHeaderParser;

public abstract class EpeStringRequest<T extends EpeRequestInfo> extends EpeRequest<T> {

    public EpeStringRequest(NetWorkHandler<T> eventHandler, T info) {
        super(eventHandler, info);
    }

    private static final String LOG_TAG = EpeStringRequest.class.getSimpleName();

    protected abstract void parseStringResponse(String response);

    @Override
    protected void parseResponse(NetworkResponse response) {

        String responseStr;
        try {
            responseStr = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            INFO.ERROR_CODE.setCode(EpeErrorCode.CODE_UNKNOW);
            INFO.ERROR_CODE.setDescription( "cause by parse body to String");
            INFO.ERROR_CODE.addDyingMsg( String.format(
                    "\n handle a throwable \n NetworkResponse = %s \n throwable = %s", response, e));
            INFO.ERROR_CODE.setThrowableError(e);
            return;
        }
        parseStringResponse(responseStr);
    }
}
