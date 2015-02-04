package tw.supra.location;

import tw.supra.epe.R;
import tw.supra.epe.core.BaseActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public class MapPickerActivity extends BaseActivity implements OnClickListener,
		OnGetGeoCoderResultListener, BDLocationListener, OnMapClickListener {

	private static final String LOG_TAG = MapPickerActivity.class
			.getSimpleName();
	public static final String RESULT_DOUBLE_LAT = "lat";
	public static final String RESULT_DOUBLE_LON = "lon";
	public static final String RESULT_STR_DESC = "desc";

	// 定位相关
	LocationClient mLocClient;
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

	// UI相关
	boolean isFirstLoc = true;// 是否首次定位

	private LatLng mLatLng;
	private String mDesc;

	/**
	 * MapView 是地图主控件
	 */
	private MapView mMapView;
	private BaiduMap mBaiduMap;

	private TextView mTvLabel;

	public static void show(Context c) {
		Intent i = new Intent(c, MapActivity.class);
		c.startActivity(i);
	}

	/**
	 * 设置布局
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_map_picker);
		mTvLabel = (TextView) findViewById(R.id.label);

		findViewById(R.id.action_back).setOnClickListener(this);
		findViewById(R.id.action_done).setOnClickListener(this);
		mMapView = (MapView) findViewById(R.id.map);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setOnMapClickListener(this);

		// 传入null则，恢复默认图标
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				LocationMode.NORMAL, true, null));

		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(this);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.action_back:
			onBackPressed();
			break;
		case R.id.action_done:
			Intent i = new Intent();
			i.putExtra(RESULT_DOUBLE_LAT, mLatLng.latitude);
			i.putExtra(RESULT_DOUBLE_LON, mLatLng.longitude);
			i.putExtra(RESULT_STR_DESC, mDesc);
			setResult(RESULT_OK, i);
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		// MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()

		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		mSearch.destroy();

		super.onDestroy();
		// 回收 bitmap 资源
	}

	@Override
	public void onMapClick(LatLng arg0) {
		mLatLng = arg0;
		mBaiduMap.clear();
		mBaiduMap
				.addOverlay(new MarkerOptions()
						.position(mLatLng)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon_gcoding))
						.draggable(true));

		// 反Geo搜索
		mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(arg0));

		findViewById(R.id.action_done).setVisibility(View.VISIBLE);

	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		return false;
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		// map view 销毁后不在处理新接收的位置
		if (location == null || mMapView == null)
			return;
		MyLocationData locData = new MyLocationData.Builder()
				.accuracy(location.getRadius())
				// 此处设置开发者获取到的方向信息，顺时针0-360
				.direction(100).latitude(location.getLatitude())
				.longitude(location.getLongitude()).build();
		mBaiduMap.setMyLocationData(locData);
		if (isFirstLoc) {
			isFirstLoc = false;
			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			mTvLabel.setText(R.string.map_picker_label);
		} else {
			mDesc = result.getAddress();
			mTvLabel.setText(mDesc);
		}

	}
}
