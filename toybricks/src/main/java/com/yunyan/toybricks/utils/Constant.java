package com.yunyan.toybricks.utils;

import android.os.Environment;

/**
 * Created by George on 2015/3/27.
 */
public class Constant {
	public static Boolean NET_CHANGE=false;
	public static Boolean KAMENG_NET=false;
    /*网络标志*/
    public final static int FAILED = 1;
    public final static int SUCCESS = 1;
    public final static int NET_FAILED = 2;
    public final static int TIME_OUT = 3;
    /*卡萌下载任务*/
    public static Boolean downloadflag=false;
    public static int toybricks=-1;
    public final static int D_START = 1000;
    public final static int D_HANDLING = 1001;
    public final static int D_COMPLETED = 1002;
    public final static int D_ERROR = 1003;
    /*解压卡萌*/
    public final static int Z_START = 10000;
    public final static int Z_HANDLING = 10001;
    public final static int Z_COMPLETED = 10002;
    public final static int Z_ERROR = 10003;

    
    public final static String PERCENT = "PERCENT";
    public final static String ERROR_COM = "ERROR";

    /*选择卡萌*/
    public final static int CHOOSE_Y=2000;
    public final static int CHOOSE_N=2001;

    
    public final static int QR_REQUESTCODE= 30001;
    
    public final static String TOYBRICKS_PATH= Environment.getExternalStorageDirectory() + "/toybricks/";
    public final static String TOYBRICKS_CACHEPATH= Environment.getExternalStorageDirectory() + "/.toybricks/";
    public final static String TOYBRICKS_SEAPATH= Environment.getExternalStorageDirectory() + "/.toybricks/ads/";

    
	public static final String WE_QQ="3123154916";
	public static final String WE_WEB="http://www.yunyankeji.com";
	public static final String WE_TEL="05785138588";
	public static final String WE_TAOBAO="http://muwanzi.tmall.com";
	public static final String WE_SHARECONTENT="朋友们，我正在玩木丸子。很好玩哦！大家也来下载吧！";
	public static final String WE_VIDEO="http://v.youku.com/v_show/id_XNTc0ODM4OTM2.html";
	public static final String WE_VIDEOTITLE="木丸子视频";
	public static final String WE_MUSIC="http://music.huoxing.com/upload/20130330/1364651263157_1085.mp3";
	public static final String WE_TITLE="木丸子";
	public static final String WE_THUMB="http://www.baidu.com/img/bdlogo.png";
	public static final String ICONURL="http://www.yunyankeji.com:1222/dzy/android/fill/Icon/";
	public static final String AILER_APP_HOST="http://www.yunyankeji.com:";
	
    public static final String DESCRIPTOR = "com.umeng.share";
	
	private static final String TIPS = "请移步官方网站 ";
	private static final String END_TIPS = ", 查看相关说明.";
	public static final String TENCENT_OPEN_URL = TIPS + "http://wiki.connect.qq.com/android_sdk使用说明" + END_TIPS;
	public static final String PERMISSION_URL = TIPS + "http://wiki.connect.qq.com/openapi权限申请" + END_TIPS;		
}
