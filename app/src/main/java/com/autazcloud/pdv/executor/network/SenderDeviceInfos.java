package com.autazcloud.pdv.executor.network;

import android.content.Context;

import com.autazcloud.pdv.domain.constants.Metadata;
import com.autazcloud.pdv.domain.constants.ServerConstants;
import com.autazcloud.pdv.domain.enums.ActionsEnum;
import com.autazcloud.pdv.domain.models.Device;
import com.autazcloud.pdv.domain.models.DeviceSend;
import com.autazcloud.pdv.domain.models.WrapObjToNetwork;
import com.autazcloud.pdv.executor.services.GPSTracker;


/**
 * Created by andre on 15/10/2015.
 */
public class SenderDeviceInfos {

    public static synchronized void syncInfos(Context mContext, ActionsEnum action ) {
    	Device device = new Device(mContext);
    	DeviceSend infos = new DeviceSend(mContext, device);
    	
        infos.addMetadata(Metadata.ACTION_SEND, action.toString());

        GPSTracker gps = GPSTracker.getInstance(mContext);
        // checa se o GPS esta habilitado
        if(gps.canGetLocation()){
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            infos.setLatitude(latitude);
            infos.setLongitude(longitude);
        }
        infos.addMetadata(Metadata.DEVICE_BRAND, infos.getDevice().getBrand());
        infos.addMetadata(Metadata.DEVICE_MODEL, infos.getDevice().getModeln());
        
        if (device.getMetadata() != null) {
	        for (String key : device.getMetadata().keySet()) {
	        	String value = device.getMetadata().get(key);
	        	infos.addMetadata(key, value);
	        }
        }
        
        WrapObjToNetwork obj = new WrapObjToNetwork(infos, "geo");
        //WrapRequestToNetwork o = new WrapRequestToNetwork(infos, "geo", Constants.SERVER_GEO);
        NetworkConnection.getInstance(mContext).execute(obj, SenderDeviceInfos.class.getName(), ServerConstants.SERVER_GEO);
    }
}