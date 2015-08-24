package com.yunyan.toybricks.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {

		String status = NetworkUtil.getConnectivityStatusString(context);
		
		if(status.equals("NotInternet")){
			if(!Constant.KAMENG_NET)
				Constant.KAMENG_NET=false;
			else
				Constant.NET_CHANGE = true;
			Toast.makeText(context, "无网络连接，请检查网络", Toast.LENGTH_LONG).show();
		}else if(status.equals("Mobile")){
			if(Constant.KAMENG_NET || !Constant.NET_CHANGE)
				Constant.KAMENG_NET=true;
			else
				Constant.NET_CHANGE = true;
			Toast.makeText(context, "连接到移动网络，下载请注意流量消耗", Toast.LENGTH_LONG).show();
		}else if(status.equals("Wifi")){
			if(Constant.KAMENG_NET || !Constant.NET_CHANGE)
				Constant.KAMENG_NET=true;
			else
				Constant.NET_CHANGE = true;
			//Toast.makeText(context, "连接到WIFI网络", Toast.LENGTH_LONG).show();
		}
	}
}
