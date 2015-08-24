package com.yunyan.toybricks.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 获取手机信息工具类<br>
 * 内部已经封装了打印功能,只需要把DEBUG参数改为true即可<br>
 * 如果需要更换tag可以直接更改,默认为KEZHUANG
 * 
 * @author KEZHUANG
 *
 */
public class DeviceUtils {

	/**
	 * 获取应用程序的IMEI号
	 */
	public static String getIMEI(Context context) {
		TelephonyManager telecomManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telecomManager.getDeviceId();
		return imei;
	}

	/**
	 * 获取设备的系统版本号
	 */
	public static int getDeviceSDK() {
		int sdk = android.os.Build.VERSION.SDK_INT;
		return sdk;
	}

	/**
	 * 获取设备的型号
	 */
	public static String getDeviceName() {
		String model = android.os.Build.MODEL;
		return model;
	}
	

    public static String getDeviceInfo(Context context)
    {
        try
        {
            TelephonyManager telephonymanager = (TelephonyManager)context.getSystemService("phone");
            String s = telephonymanager.getDeviceId();
            WifiManager wifimanager = (WifiManager)context.getSystemService("wifi");
            String s1 = wifimanager.getConnectionInfo().getMacAddress();
            if(TextUtils.isEmpty(s))
                s = s1;
            if(TextUtils.isEmpty(s))
                s = android.provider.Settings.Secure.getString(context.getContentResolver(), "android_id");
            return s;
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
        return null;
    }	
}
