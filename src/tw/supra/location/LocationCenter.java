package tw.supra.location;

import tw.supra.epe.App;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationCenter implements LocationListener {
	private static final String LOG_TAG = LocationCenter.class.getSimpleName();

	private static final int TWO_MINUTES = 1000 * 60 * 2;

	private static LocationCenter sInstance;
	private Location mCurrentBestLocation;

	public static LocationCenter getInstance() {
		if (null == sInstance) {
			sInstance = new LocationCenter();
		}
		return sInstance;
	}

	private LocationCenter() {
	}

	public String getBestProvider() {
		LocationManager locationManager = (LocationManager) App.getInstance()
				.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria(); // Criteria是标准条件的意思，也就是定位所需的条件准备
		String provider = locationManager.getBestProvider(criteria, true);
//		String provider = LocationManager.NETWORK_PROVIDER;
		Log.i(LOG_TAG, "getBestProvider : " + provider);
		
		
		Log.i(LOG_TAG, "getAllProviders : " + locationManager.getAllProviders());
		
		return provider;
	}

	private void updateCurrentBestLocation(Location location){
		if(isBetterLocation(location, mCurrentBestLocation)){
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
//		if(!isBetterLocation(location, mCurrentBestLocation)){
//		// Acquire a reference to the system Location Manager
//		LocationManager locationManager = (LocationManager) App.getInstance()
//				.getSystemService(Context.LOCATION_SERVICE);
//
//		// Register the listener with the Location Manager to receive location
//		// updates
//	}
//		locationManager.requestLocationUpdates(getBestProvider(), 1000, 0, this);
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

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	// Define a listener that responds to location updates
	public void onLocationChanged(Location location) {
		// Called when a new location is found by the network location
		// provider.
		// TODO Auto-generated method stub
		Log.i(LOG_TAG, "onLocationChanged : " + location);
	}

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


}
