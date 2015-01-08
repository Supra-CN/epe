
package tw.supra.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class PhotoPager extends ViewPager {
    private OnDispatchTouchListener mListener;
    
    public PhotoPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotoPager(Context context) {
        super(context);
    }
    
    public void setOnDispatchTouchListener(OnDispatchTouchListener listener){
        mListener = listener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(null != mListener){
            if(mListener.onDispatchTouchEvent(ev)){
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    public interface OnDispatchTouchListener {
        public boolean onDispatchTouchEvent(MotionEvent ev);
    }
}
