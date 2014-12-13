
package tw.supra.epe.core;


public abstract class BaseHostFrag<T extends BaseActivity> extends BaseFrag {

    protected T getHostActivity() {
         return (T) getActivity();
    }

}
