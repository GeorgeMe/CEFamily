package com.yunyan.toybricks.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.yunyan.toybricks.view.ToyBricksMainActivity;
import com.yunyan.toybricks.view.ToyBricksShareActivity;

import android.app.Activity;
import android.content.Intent;


/**
 * ClassName:MethodControlTest 
 * Date:     2014年11月10日 下午5:02:53 
 * @author   ZoZo
 * @version  版本号
 * @since    JDK 1.7
 * @see 	 
 */

public class UnityGetAndroidUI {
	
	private static UnityGetAndroidUI methodControlTest;
	private Class<?> _mClass;
	private Field _mActivityField;
	private Method _mSendMessageMethod;
	private static final Class<?> String = null;
	public Activity _activity;
	//单例
	public static UnityGetAndroidUI instance(){
		if(methodControlTest==null){
			methodControlTest=new UnityGetAndroidUI();
		}
		return methodControlTest;
	} 
	
	//得到Unity相关信息
	public UnityGetAndroidUI() {
		try {
			_mClass = Class.forName("com.unity3d.player.UnityPlayer");
			_mActivityField = _mClass.getField("currentActivity");
			_mSendMessageMethod = _mClass.getMethod("UnitySendMessage", new Class[] { String, String, String });
		} catch (ClassNotFoundException e) {
		} catch (NoSuchFieldException e) {
		} catch (Exception e) {
		}
	}
	
	//Unity与Android通讯的方法
	public  void UnitySendMessage(String go, String m, String p) {
		try {
			if (_mSendMessageMethod != null){
				_mSendMessageMethod.invoke(null, new Object[] { go, m, p });
			}
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
	}
	
	//得到activity
	public Activity getActivity() {
		try {
			if (_mActivityField == null) {
				return null;
			} else{
				return (Activity) _mActivityField.get(_mClass);
			}
		} catch (Exception e) {
			return _activity;
		}
	}
	
	//启动设置界面
	public void startAppSetting(){
		Intent Setting = new Intent(getActivity(),ToyBricksMainActivity.class);
		getActivity().startActivity(Setting);
	}

	//分享截图
	public void savePhoto(){
		Intent intent=new Intent();
		intent.setClass(getActivity(), ToyBricksShareActivity.class);
		getActivity().startActivity(intent);
	}
	
	public void Log(String tag, String msg){
		LogUtils.e(tag, msg);
	}

	public void savePath(String path){
		XmlDB.getInstance(getActivity()).saveKey("path", path);
	}
	public boolean xml_sp(String key){
		return XmlDB.getInstance(getActivity().getApplicationContext()).getKeyBooleanValue(key,false);
				
	}
	public void xml_ps(String mKey,Boolean mValue){
		XmlDB.getInstance(getActivity().getApplicationContext()).saveKey(mKey, mValue);;
				
	}	
}

