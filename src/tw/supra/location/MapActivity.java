package tw.supra.location;

import tw.supra.epe.R;
import tw.supra.epe.core.BaseActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;

public class MapActivity extends BaseActivity implements OnClickListener {

	private static final String LOG_TAG = MapActivity.class.getSimpleName();
	public static final String EXTRA_DOUBLE_LAT = "lat";
	public static final String EXTRA_DOUBLE_LON = "lon";
	public static final String EXTRA_STR_LABEL = "label";

	/**
	 * MapView 是地图主控件
	 */
	private MapView mMapView;
	private BaiduMap mBaiduMap;

	public static void show(Context c, String label, double lat, double lon) {
		Intent i = new Intent(c, MapActivity.class);
		i.putExtra(EXTRA_STR_LABEL, label);
		i.putExtra(EXTRA_DOUBLE_LAT, lat);
		i.putExtra(EXTRA_DOUBLE_LON, lon);
		c.startActivity(i);
	}

	/**
	 * 设置布局
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		Intent intent = getIntent();
		((TextView) findViewById(R.id.label)).setText(intent
				.getStringExtra(EXTRA_STR_LABEL));

		// 当用intent参数时，设置中心点为指定点
		LatLng p = new LatLng(intent.getDoubleExtra(EXTRA_DOUBLE_LAT, 0),
				intent.getDoubleExtra(EXTRA_DOUBLE_LON, 0));

		findViewById(R.id.action_back).setOnClickListener(this);
		mMapView = (MapView) findViewById(R.id.map);
		mBaiduMap = mMapView.getMap();

		mBaiduMap.clear();

		mBaiduMap.addOverlay(new MarkerOptions().position(p).icon(
				BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding)));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(p));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.action_back:
			onBackPressed();
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
		mMapView.onDestroy();
		super.onDestroy();
	}

}
