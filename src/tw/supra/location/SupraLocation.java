package tw.supra.location;

import android.location.Location;
import android.os.Bundle;

import com.baidu.location.BDLocation;

public class SupraLocation extends Location {

	private BDLocation mBdLocation;

	public SupraLocation(String provider) {
		super(provider);
	}

	public SupraLocation(Location l) {
		super(l);
	}

	public SupraLocation(BDLocation l) {
		super(LocationCenter.BAIDU_PROVIDER);
		set(l);
	}

	/**
	 * Sets the contents of the location to the values from the given location.
	 */
	public void set(BDLocation l) {
		mBdLocation = l;
		setProvider(LocationCenter.BAIDU_PROVIDER);
//		mTime = l.mTime;
//		mElapsedRealtimeNanos = l.mElapsedRealtimeNanos;
		setLatitude(l.getLatitude());
		setLongitude(l.getLongitude());
		setAltitude(l.getAltitude());
		setSpeed(l.getSpeed());
//		mHasAltitude = l.mHasAltitude;
//		mHasSpeed = l.mHasSpeed;
//		mHasBearing = l.mHasBearing;
//		mBearing = l.mBearing;
//		mHasAccuracy = l.mHasAccuracy;
//		mAccuracy = l.mAccuracy;
		
//		mExtras = (l.mExtras == null) ? null : new Bundle(l.mExtras);
//		mIsFromMockProvider = l.mIsFromMockProvider;
	}

	public BDLocation getBdLocation() {
		return mBdLocation;
	}

}
