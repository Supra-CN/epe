package tw.supra.location;

import java.util.HashSet;

import tw.supra.epe.App;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;

public class LocationCenter {
	private static final String LOG_TAG = LocationCenter.class.getSimpleName();
	/**
	 * Name of the network location provider.
	 * <p>
	 * This provider determines location based on availability of cell tower and
	 * WiFi access points. Results are retrieved by means of a network lookup.
	 */
	public static final String BAIDU_PROVIDER = "baidu";

	private static final int TWO_MINUTES = 1000 * 60 * 2;

	private static LocationCenter sInstance;
	private Location mCurrentBestLocation;

	private SupraLocation mBestLocation;

	private final HashSet<LocationCallBack> CALL_BACKS = new HashSet<LocationCallBack>();

	public LocationClient mLocationClient;
	public final BDLocationListener BAIDU_LOCATION_LISTENER = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null){
				return;
			}
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			}
			Log.i(LOG_TAG, sb.toString());
			mBestLocation =new SupraLocation(location);
			notifyCallBacks();
		}
	};

	public static LocationCenter getInstance() {
		if (null == sInstance) {
			sInstance = new LocationCenter();
		}
		return sInstance;
	}

	private LocationCenter() {
	}

	public void startUp(App app) {
		SDKInitializer.initialize(app);
		mLocationClient = new LocationClient(app); // 声明LocationClient类
		// LocationClientOption clientOption = new LocationClientOption();
		// mLocationClient.setLocOption(clientOption);
		mLocationClient.registerLocationListener(BAIDU_LOCATION_LISTENER); // 注册监听函数
		mLocationClient.start();
	}

	public String getBestProvider() {
		LocationManager locationManager = (LocationManager) App.getInstance()
				.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria(); // Criteria是标准条件的意思，也就是定位所需的条件准备
		String provider = locationManager.getBestProvider(criteria, true);
		// String provider = LocationManager.NETWORK_PROVIDER;
		Log.i(LOG_TAG, "getBestProvider : " + provider);

		Log.i(LOG_TAG, "getAllProviders : " + locationManager.getAllProviders());

		return provider;
	}

	private void updateCurrentBestLocation(Location location) {
		if (isBetterLocation(location, mCurrentBestLocation)) {
			mCurrentBestLocation = location;
		}
	}

	public Location getLastKnownLocation() {
		if (null == mCurrentBestLocation) {
			LocationManager locationManager = (LocationManager) App
					.getInstance().getSystemService(Context.LOCATION_SERVICE);
			updateCurrentBestLocation(locationManager
					.getLastKnownLocation(getBestProvider()));
		}
		Log.i(LOG_TAG, "getLastKnownLocation : " + mCurrentBestLocation);
		return mCurrentBestLocation;
	}

	public Location requestLocationUpdates(boolean force) {
		Location location = getLastKnownLocation();
		// if(!isBetterLocation(location, mCurrentBestLocation)){
		// // Acquire a reference to the system Location Manager
		// LocationManager locationManager = (LocationManager) App.getInstance()
		// .getSystemService(Context.LOCATION_SERVICE);
		//
		// // Register the listener with the Location Manager to receive
		// location
		// // updates
		// }
		// locationManager.requestLocationUpdates(getBestProvider(), 1000, 0,
		// this);
		return location;
	}

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */
	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */
	protected boolean isBetterLocation(SupraLocation location,
			SupraLocation currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	private final LocationListener LOCATION_LISTENER = new LocationListener() {
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.i(LOG_TAG, "onLocationChanged : provider = " + provider
					+ " status = " + status);
			// TODO Auto-generated method stub
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.i(LOG_TAG, "onProviderEnabled : " + provider);
			// TODO Auto-generated method stub
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.i(LOG_TAG, "onProviderDisabled : " + provider);
			// TODO Auto-generated method stub
		}

		// Define a listener that responds to location updates
		@Override
		public void onLocationChanged(Location location) {
			// Called when a new location is found by the network location
			// provider.
			// TODO Auto-generated method stub
			Log.i(LOG_TAG, "onLocationChanged : " + location);

			SupraLocation supraLocation = new SupraLocation(location);
			if (isBetterLocation(supraLocation, mBestLocation)) {
				mBestLocation = supraLocation;
			}
			notifyCallBacks();
		}
	};

	public void requestLocation(LocationCallBack callBack) {
		CALL_BACKS.add(callBack);
		if (hasBestLoction()) {
			notifyCallBacks();
		}
		requestLocation();
	}

	private void requestLocation() {
//		((LocationManager) App.getInstance().getSystemService(
//				Context.LOCATION_SERVICE)).requestSingleUpdate(LocationCenter
//				.getInstance().getBestProvider(), LOCATION_LISTENER, Looper
//				.getMainLooper());
		mLocationClient.requestLocation();
	}

	private boolean hasBestLoction() {
		return mBestLocation != null;
	}

	private void notifyCallBacks() {
		for (LocationCallBack callBack : CALL_BACKS) {
			callBack.callBack(mBestLocation);
		}
		CALL_BACKS.clear();
	}

}
