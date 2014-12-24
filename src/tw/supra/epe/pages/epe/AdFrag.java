package tw.supra.epe.pages.epe;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.toolbox.NetworkImageView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import tw.supra.epe.core.BaseFrag;
import tw.supra.network.NetworkCenter;

public class AdFrag extends BaseFrag {
	private final JSONObject JO_AD;
	
	public AdFrag(JSONObject joAd) {
		JO_AD = joAd;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		String img;
		try {
			img = JO_AD.getString(RequestAds.ATTR_IMG_URL);
		} catch (JSONException e) {
			e.printStackTrace();
			return super.onCreateView(inflater, container, savedInstanceState);
		}
		
		NetworkImageView iv = new NetworkImageView(getActivity());
		iv.setImageUrl(img, NetworkCenter.getInstance().getImageLoader());
		return iv;
	}

	@Override
	protected CharSequence getDefaultTitle(Context c) {
		return "";
	}

	@Override
	public int getIconResId() {
		return 0;
	}
	
}
