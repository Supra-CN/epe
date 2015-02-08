package tw.supra.epe.store;

import java.util.HashMap;

import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;

public class RequestApplyMallStore extends RequestApplyStore {
	private static final String LOG_TAG = RequestApplyMallStore.class
			.getSimpleName();

	private final String FLOOR_NUM;
	private final String MALL_ID;
	private final String BRAND_ID;
	private final String DOOR_NO;

	public  RequestApplyMallStore(NetWorkHandler<EpeRequestInfo> eventHandler,
			String code, String mobile, ObjArea area, String mallId,
			ObjShop store, String doorNo) {
		super(eventHandler, MALL_TYPE_MALL, code, mobile, area);
		MALL_ID = mallId;
		FLOOR_NUM = store.floor;
		BRAND_ID = store.brandId;
		DOOR_NO = doorNo;
	}

	protected void fillAppleyParamters(HashMap<String, String> p) {
		p.put("floor_id", FLOOR_NUM);
		p.put("mall_id", MALL_ID);
		p.put("brand_id", BRAND_ID);
		p.put("door_num", DOOR_NO);
	}

}
