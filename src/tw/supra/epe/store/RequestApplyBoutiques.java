package tw.supra.epe.store;

import java.util.HashMap;

import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;

import com.baidu.mapapi.model.LatLng;

public class RequestApplyBoutiques extends RequestApplyStore {
	private static final String LOG_TAG = RequestApplyBoutiques.class
			.getSimpleName();

	private final String MALL_NAME;
	private final LatLng LAT_LNG;
	private final String STREET;

	public RequestApplyBoutiques(NetWorkHandler<EpeRequestInfo> eventHandler,
			String code, String mobile, ObjArea area, String name,
			LatLng latLng, String street) {
		super(eventHandler, MALL_TYPE_BOUTIQUES, code, mobile, area);
		MALL_NAME = name;
		LAT_LNG = latLng;
		STREET = street;
	}

	protected void fillAppleyParamters(HashMap<String, String> p) {
		p.put("mall_name", MALL_NAME);
		p.put("street", STREET);
		p.put("latitude", String.valueOf(LAT_LNG.latitude));
		p.put("longitude", String.valueOf(LAT_LNG.longitude));
	}
}
