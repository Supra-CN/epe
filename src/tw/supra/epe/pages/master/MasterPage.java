package tw.supra.epe.pages.master;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.account.AccountCenter;
import tw.supra.epe.core.BaseMainPage;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import tw.supra.network.ui.NetworkImageView;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.viewpagerindicator.PageIndicator;
import com.yijiayi.yijiayi.R;

public class MasterPage extends BaseMainPage {
	private static final String LOG_TAG = MasterPage.class.getSimpleName();

	private static final Class<?>[] PAGES = { TopicPage.class, CustomPage.class };

	private ViewGroup mMyMasterContainer;
	private ViewGroup mTopMasterContainer;
	private ViewGroup mTagsContainer;

	private PageAdapter mAdapter;
	private PageIndicator mPageIndicator;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = (View) inflater.inflate(R.layout.page_master, null);
		mMyMasterContainer = (ViewGroup) v
				.findViewById(R.id.my_master_container);
		mTopMasterContainer = (ViewGroup) v
				.findViewById(R.id.top_master_container);
		mTagsContainer = (ViewGroup) v.findViewById(R.id.tags_container);

		ViewPager viewPager = (ViewPager) v.findViewById(R.id.view_pager);
		mAdapter = new PageAdapter(getChildFragmentManager());
		viewPager.setAdapter(mAdapter);
		mPageIndicator = (PageIndicator) v.findViewById(R.id.page_indicator);
		mPageIndicator.setViewPager(viewPager);

		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		requestTags();
		requestMyMaster();
		requestTopMaster(null);
	}

	@Override
	protected CharSequence getDefaultTitle(Context c) {
		return c.getText(R.string.indictor_tab_master);
	}

	@Override
	public int getIconResId() {
		return 0;
	}

	private void requestMyMaster() {
		Log.i(LOG_TAG, "requestMyMaster");
		NetworkCenter.getInstance().putToQueue(
				new RequestMaster(HANDLER_MY_MASTER, new MasterInfo(
						MasterInfo.ACTION_MY,
						AccountCenter.getCurrentUserUid(), null, 1)));
	}

	private void requestTags() {
		Log.i(LOG_TAG, "requestMyMaster");
		NetworkCenter.getInstance()
				.putToQueue(new RequestTags(HANDLER_TAGS, 1));
	}

	private void requestTopMaster(String tagId) {
		Log.i(LOG_TAG, "requestTopMaster : " + tagId);
		NetworkCenter.getInstance().putToQueue(
				new RequestMaster(HANDLER_TOP_MASTER, new MasterInfo(
						MasterInfo.ACTION_POPU, null, tagId, 1)));
	}

	private final NetWorkHandler<EpeRequestInfo> HANDLER_TAGS = new NetWorkHandler<EpeRequestInfo>() {

		@Override
		public boolean HandleEvent(RequestEvent event, EpeRequestInfo info) {
			if (RequestEvent.FINISH == event) {

				if (info.ERROR_CODE.isOK()) {
					mTagsContainer.removeAllViews();
					Log.i(LOG_TAG, "HANDLER_TAGS : " + info.OBJ);

					if (info.OBJ instanceof JSONArray) {

						JSONArray ja = (JSONArray) info.OBJ;
						for (int i = 0; i < ja.length(); i++) {
							try {
								View v = createTagView(ja.getJSONObject(i));
								mTagsContainer.addView(v, new LayoutParams(
										LayoutParams.WRAP_CONTENT,
										LayoutParams.MATCH_PARENT));
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			return false;
		}
	};
	private final NetWorkHandler<MasterInfo> HANDLER_MY_MASTER = new NetWorkHandler<MasterInfo>() {

		@Override
		public boolean HandleEvent(RequestEvent event, MasterInfo info) {
			if (RequestEvent.FINISH == event) {

				if (info.ERROR_CODE.isOK()) {
					mMyMasterContainer.removeAllViews();
					JSONArray ja = info.resultJoList;
					for (int i = 0; i < ja.length(); i++) {
						try {
							View v = createMasterView(ja.getJSONObject(i));
							mMyMasterContainer.addView(v, new LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.MATCH_PARENT));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}
			return false;
		}
	};
	private final NetWorkHandler<MasterInfo> HANDLER_TOP_MASTER = new NetWorkHandler<MasterInfo>() {

		@Override
		public boolean HandleEvent(RequestEvent event, MasterInfo info) {
			if (RequestEvent.FINISH == event) {

				if (info.ERROR_CODE.isOK()) {
					mTopMasterContainer.removeAllViews();
					JSONArray ja = info.resultJoList;
					for (int i = 0; i < ja.length(); i++) {
						try {
							View v = createMasterView(ja.getJSONObject(i));
							mTopMasterContainer.addView(v, new LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.MATCH_PARENT));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}
			return false;
		}
	};

	private View createMasterView(JSONObject joItem) throws JSONException {
		View v = View.inflate(getActivity(), R.layout.master_page_master_item,
				null);
		TextView tv = (TextView) v.findViewById(R.id.text);
		NetworkImageView iv = (NetworkImageView) v.findViewById(R.id.img);
		RatingBar ratingBar = (RatingBar) v.findViewById(R.id.rating);

		String name = joItem.getString(MasterInfo.ATTR_NAME);
		String avator = joItem.getString(MasterInfo.ATTR_PHOTO);
		double rating = joItem.getDouble((MasterInfo.ATTR_GRADE));

		tv.setText(name);
		iv.setImageUrl(avator, NetworkCenter.getInstance().getImageLoader());
		ratingBar.setRating(Double.valueOf(rating).floatValue());
		return v;
	}

	private View createTagView(JSONObject joItem) throws JSONException {
		Log.i(LOG_TAG, "createTagView : " + joItem);
		Button v = new Button(getActivity());
		String name = joItem.getString(RequestTags.ATTR_NAME);
		v.setText(name);
		v.setTag(joItem.getString(RequestTags.ATTR_ID));
		v.setOnClickListener(ON_TAG_CLICK);
		return v;
	}

	private final OnClickListener ON_TAG_CLICK = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String tag = (String) v.getTag();
			if (!TextUtils.isEmpty(tag)) {
				requestTopMaster(tag);
			}
		}
	};

	public class PageAdapter extends FragmentPagerAdapter {

		private final HashMap<Class<?>, BaseMainPage> PAGE_POOL = new HashMap<Class<?>, BaseMainPage>();

		public PageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return PAGES.length;
		}

		@Override
		public BaseMainPage getItem(int position) {
			BaseMainPage page = PAGE_POOL.get(PAGES[position]);
			if (null == page) {
				try {
					page = (BaseMainPage) PAGES[position].newInstance();
					PAGE_POOL.put(PAGES[position], page);
				} catch (InstantiationException e) {
					e.printStackTrace();
					throw new IllegalStateException(String.format(
							"the page %s is not a legal page",
							PAGES[position].getSimpleName()), e);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					throw new IllegalStateException(String.format(
							"the page %s is not a legal page",
							PAGES[position].getSimpleName()), e);
				} catch (java.lang.InstantiationException e) {
					e.printStackTrace();
					throw new IllegalStateException(String.format(
							"the page %s is not a legal page",
							PAGES[position].getSimpleName()), e);
				}
			}
			// Log.i(LOG_TAG, "getItem :  pos =" + position + " page" + page);
			return page;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getItem(position).getTitle();
		}

	}

}
