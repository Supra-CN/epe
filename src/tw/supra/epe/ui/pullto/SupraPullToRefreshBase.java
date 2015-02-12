package tw.supra.epe.ui.pullto;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshBase;

public abstract class SupraPullToRefreshBase<T extends View> extends
		PullToRefreshBase<T> {

	public SupraPullToRefreshBase(
			Context context,
			com.handmark.pulltorefresh.library.PullToRefreshBase.Mode mode,
			com.handmark.pulltorefresh.library.PullToRefreshBase.AnimationStyle animStyle) {
		super(context, mode, animStyle);
	}

	public SupraPullToRefreshBase(Context context,
			com.handmark.pulltorefresh.library.PullToRefreshBase.Mode mode) {
		super(context, mode);
	}

	public SupraPullToRefreshBase(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SupraPullToRefreshBase(Context context) {
		super(context);
	}

}
