
package tw.supra.epe.pages;

import tw.supra.epe.core.BaseFrag;
import tw.supra.network.NetworkCenter;
import tw.supra.network.Response.Listener;
import tw.supra.network.ui.NetworkImageView;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yijiayi.yijiayi.R;


public class PhotoClient extends BaseFrag implements Listener<Bitmap> {
    private static final String LOG_TAG = PhotoClient.class.getSimpleName();
    public static final String ARG_STRING_IMG_URL = "arg_string_img_url";

    private String mImgUrl;

//    private PhotoView mPhotoView;
    private NetworkImageView mPhotoView;
    private View mProgressBar;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle arg = getArguments();
        mImgUrl = arg.getString(ARG_STRING_IMG_URL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = View.inflate(getActivity(), R.layout.client_photo, null);
//        mPhotoView = (PhotoView)root.findViewById(R.id.photo_view);
        mPhotoView = (NetworkImageView)root.findViewById(R.id.photo_view);
        mProgressBar =root.findViewById(R.id.progress_bar);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPhotoView.setImageListener(this);
//        mPhotoView.enableImageTransforms(true);
        mPhotoView.setImageUrl(mImgUrl, NetworkCenter.getInstance().getImageLoader());
//        mPhotoView.setFullScreen(true, true);
    }
    

    @Override
    public void onResponse(Bitmap response) {
        mProgressBar.setVisibility(View.GONE);
    }

	@Override
	protected CharSequence getDefaultTitle(Context c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getIconResId() {
		// TODO Auto-generated method stub
		return 0;
	}
}
